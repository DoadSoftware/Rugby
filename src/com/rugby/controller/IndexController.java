package com.rugby.controller;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rugby.broadcaster.Super_Cup;
import com.opencsv.exceptions.CsvException;
import com.rugby.broadcaster.KHELO_INDIA;
import com.rugby.containers.Scene;
import com.rugby.containers.ScoreBug;
import com.rugby.model.Clock;
import com.rugby.model.Configurations;
import com.rugby.model.Event;
import com.rugby.model.EventFile;
import com.rugby.model.LiveMatchData;
import com.rugby.model.Match;
import com.rugby.model.MatchStats;
import com.rugby.model.Player;
import com.rugby.service.RugbyService;
import com.rugby.util.RugbyFunctions;
import com.rugby.util.RugbyUtil;

import net.sf.json.JSONObject;

@Controller
public class IndexController 
{
	@Autowired
	RugbyService rugbyService;
	
	public static String expiry_date = "2024-12-31";
	public static String current_date = "";
	public static String error_message = "";
	public static Clock session_clock = new Clock();
	public static Configurations session_configurations;
	public static Match session_match;
	public static EventFile session_event;
	public static String session_selected_broadcaster;
	public static Socket session_socket;
	public static KHELO_INDIA session_khelo_india;
	public static Super_Cup session_super_cup;
	public static List<Scene> session_selected_scenes;
	
	@RequestMapping(value = {"/","/initialise"}, method={RequestMethod.GET,RequestMethod.POST}) 
	public String initialisePage(ModelMap model) 
		throws IOException, JAXBException 
	{
		
		if(current_date == null || current_date.isEmpty()) {
			current_date = RugbyFunctions.getOnlineCurrentDate();
		}
		model.addAttribute("session_viz_scenes", new File(RugbyUtil.RUGBY_DIRECTORY + 
				RugbyUtil.SCENES_DIRECTORY).listFiles(new FileFilter() {
			@Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".via") && pathname.isFile();
		    }
		}));

		model.addAttribute("match_files", new File(RugbyUtil.RUGBY_DIRECTORY 
				+ RugbyUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
			@Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".xml") && pathname.isFile();
		    }
		}));
		
		if(new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.CONFIGURATIONS_DIRECTORY + RugbyUtil.OUTPUT_XML).exists()) {
			session_configurations = (Configurations)JAXBContext.newInstance(
					Configurations.class).createUnmarshaller().unmarshal(
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.CONFIGURATIONS_DIRECTORY 
					+ RugbyUtil.OUTPUT_XML));
		} else {
			session_configurations = new Configurations();
			JAXBContext.newInstance(Configurations.class).createMarshaller().marshal(session_configurations, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.CONFIGURATIONS_DIRECTORY + 
					RugbyUtil.OUTPUT_XML));
		}
		
		model.addAttribute("session_configurations",session_configurations);
	
		return "initialise";
	}
	
	@RequestMapping(value = {"/setup"}, method = RequestMethod.POST)
	public String setupPage(ModelMap model) throws JAXBException, IllegalAccessException, 
		InvocationTargetException, IOException, ParseException  
	{
		model.addAttribute("match_files", new File(RugbyUtil.RUGBY_DIRECTORY + 
				RugbyUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
			@Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".xml") && pathname.isFile();
		    }
		}));
		model.addAttribute("session_match", session_match);
		model.addAttribute("teams", rugbyService.getTeams());
		//model.addAttribute("formations", rugbyService.getFormations());
		model.addAttribute("teamcolor", rugbyService.getTeamColors());
		model.addAttribute("grounds", rugbyService.getGrounds());
		model.addAttribute("licence_expiry_message",
				"Software licence expires on " + new SimpleDateFormat("E, dd MMM yyyy").format(
				new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date)));

		return "setup";
	}

	@RequestMapping(value = {"/match"}, method = {RequestMethod.POST,RequestMethod.GET})
	public String rugbyMatchPage(ModelMap model,
		@RequestParam(value = "selectedBroadcaster", required = false, defaultValue = "") String selectedBroadcaster,
		@RequestParam(value = "vizIPAddress", required = false, defaultValue = "") String vizIPAddresss,
		@RequestParam(value = "vizPortNumber", required = false, defaultValue = "") String vizPortNumber,
		@RequestParam(value = "vizScene", required = false, defaultValue = "") String vizScene)
			throws IOException, ParseException, JAXBException, InterruptedException  
	{
		if(current_date == null || current_date.isEmpty()) {
		
			model.addAttribute("error_message","You must be connected to the internet online");
			return "error";
		
		} else if(new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date).before(new SimpleDateFormat("yyyy-MM-dd").parse(current_date))) {
			
			model.addAttribute("error_message","This software has expired");
			return "error";
			
		}else {

			session_selected_broadcaster = selectedBroadcaster;
			session_selected_scenes = new ArrayList<Scene>();
			if(!vizIPAddresss.trim().isEmpty() && !vizPortNumber.trim().isEmpty()) {
				//System.out.println("Broad : " + session_selected_broadcaster + " Port : " + vizPortNumber);
				session_socket = new Socket(vizIPAddresss, Integer.valueOf(vizPortNumber));
				switch (session_selected_broadcaster.toUpperCase()) {
				case RugbyUtil.KHELO_INDIA:
					session_selected_scenes.add(new Scene(RugbyUtil.KHELO_INDIA_SCORE_BUG_SCENE_PATH,RugbyUtil.ONE)); // Front layer
					session_selected_scenes.add(new Scene("",RugbyUtil.TWO));
					session_selected_scenes.get(0).scene_load(session_socket, session_selected_broadcaster);
					session_khelo_india = new KHELO_INDIA();
					session_khelo_india.scorebug = new ScoreBug();
					break;
				case RugbyUtil.SUPER_CUP:
					session_selected_scenes.add(new Scene(RugbyUtil.SUPER_CUP_SCORE_BUG_SCENE_PATH,RugbyUtil.FRONT_LAYER)); // Front layer
					session_selected_scenes.add(new Scene("",RugbyUtil.MIDDLE_LAYER));
					session_selected_scenes.get(0).scene_load(session_socket, session_selected_broadcaster);
					session_super_cup = new Super_Cup();
					session_super_cup.scorebug = new ScoreBug();
					break;	
				}
			}
			
			model.addAttribute("match_files", new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
				@Override
			    public boolean accept(File pathname) {
			        String name = pathname.getName().toLowerCase();
			        return name.endsWith(".xml") && pathname.isFile();
			    }
			}));

			model.addAttribute("licence_expiry_message",
				"Software licence expires on " + new SimpleDateFormat("E, dd MMM yyyy").format(
				new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date)));
			
			session_match = new Match();
			session_event = new EventFile();
			if(session_event.getEvents() == null || session_event.getEvents().size() <= 0)
				session_event.setEvents(new ArrayList<Event>());
			if(session_match.getMatchStats() == null || session_match.getMatchStats().size() <= 0) 
				session_match.setMatchStats(new ArrayList<MatchStats>());
			if(session_match.getClock() == null) 
				session_match.setClock(new Clock());
			
			session_configurations.setBroadcaster(selectedBroadcaster);
			session_configurations.setVizscene(vizScene);
			session_configurations.setIpAddress(vizIPAddresss);
			
			if(!vizPortNumber.trim().isEmpty()) {
				session_configurations.setPortNumber(Integer.valueOf(vizPortNumber));
			}

			JAXBContext.newInstance(Configurations.class).createMarshaller().marshal(session_configurations, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.CONFIGURATIONS_DIRECTORY + 
					RugbyUtil.OUTPUT_XML));
			
			model.addAttribute("session_selected_broadcaster", session_selected_broadcaster);
			model.addAttribute("session_match", session_match);
			model.addAttribute("session_event", session_event);
			model.addAttribute("session_configurations", session_configurations);
			model.addAttribute("session_socket", session_socket);
			model.addAttribute("session_khelo_india", session_khelo_india);
			model.addAttribute("session_selected_scenes", session_selected_scenes);
			
			return "match";
		}
	}
	
	@RequestMapping(value = {"/back_to_match"}, method = RequestMethod.POST)
	public String backToMatchPage(ModelMap model) throws ParseException
	{
		if(current_date == null || current_date.isEmpty()) {
		
			model.addAttribute("error_message","You must be connected to the internet online");
			return "error";
		
		} else if(new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date).before(new SimpleDateFormat("yyyy-MM-dd").parse(current_date))) {
			
			model.addAttribute("error_message","This software has expired");
			return "error";
			
		}else {
		
			model.addAttribute("match_files", new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
				@Override
			    public boolean accept(File pathname) {
			        String name = pathname.getName().toLowerCase();
			        return name.endsWith(".xml") && pathname.isFile();
			    }
			}));
			model.addAttribute("licence_expiry_message",
				"Software licence expires on " + new SimpleDateFormat("E, dd MMM yyyy").format(
				new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date)));
			
			model.addAttribute("session_selected_broadcaster", session_selected_broadcaster);
			model.addAttribute("session_match", session_match);

			return "match";
		
		}
	}	
	
	@RequestMapping(value = {"/upload_match_setup_data", "/reset_and_upload_match_setup_data"}
		,method={RequestMethod.GET,RequestMethod.POST})    
	public @ResponseBody String uploadFormDataToSessionObjects(MultipartHttpServletRequest request) 
			throws IllegalAccessException, InvocationTargetException, JAXBException, IOException
	{
		if (request.getRequestURI().contains("upload_match_setup_data") 
				|| request.getRequestURI().contains("reset_and_upload_match_setup_data")) {
			
			List<Player> home_squad = new ArrayList<Player>(); List<Player> away_squad = new ArrayList<Player>();
			List<Player> home_substitutes = new ArrayList<Player>(); List<Player> away_substitutes = new ArrayList<Player>();

	   		boolean reset_all_variables = false;
			if(request.getRequestURI().contains("reset_and_upload_match_setup_data")) {
				reset_all_variables = true;
			} else if(request.getRequestURI().contains("upload_match_setup_data")) {
				for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
					if(entry.getKey().equalsIgnoreCase("select_existing_rugby_matches") && entry.getValue()[0].equalsIgnoreCase("new_match")) {
						reset_all_variables = true;
						break;
					}
				}
			}
			if(reset_all_variables == true) {
				session_match = new Match(); 
				session_event = new EventFile();
				session_event.setEvents(new ArrayList<Event>());
				session_match.setMatchStats(new ArrayList<MatchStats>());
			}
			
			for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
	   			if(entry.getKey().contains("_")) {
   					if(entry.getKey().split("_")[0].equalsIgnoreCase(RugbyUtil.HOME + RugbyUtil.PLAYER)) {
   						switch (Integer.parseInt(entry.getKey().split("_")[1])) {
   						case 1: case 2: case 3: case 4: case 5: case 6:
   						case 7: case 8: case 9: case 10: case 11:
   		   					home_squad.add(new Player(Integer.parseInt(entry.getValue()[0]), 
   		   							Integer.parseInt(entry.getKey().split("_")[1]), RugbyUtil.PLAYER));
   							break;
   						default:
   		   					home_substitutes.add(new Player(Integer.parseInt(entry.getValue()[0]), 
   		   							Integer.parseInt(entry.getKey().split("_")[1]), RugbyUtil.SUBSTITUTE));
   							break;
   						}
   					} else if(entry.getKey().split("_")[0].equalsIgnoreCase(RugbyUtil.AWAY + RugbyUtil.PLAYER)) {
   						switch (Integer.parseInt(entry.getKey().split("_")[1])) {
   						case 1: case 2: case 3: case 4: case 5: case 6:
   						case 7: case 8: case 9: case 10: case 11:
   		   					away_squad.add(new Player(Integer.parseInt(entry.getValue()[0]), 
   		   							Integer.parseInt(entry.getKey().split("_")[1]), RugbyUtil.PLAYER));
   							break;
   						default:
   		   					away_substitutes.add(new Player(Integer.parseInt(entry.getValue()[0]), 
   		   							Integer.parseInt(entry.getKey().split("_")[1]), RugbyUtil.SUBSTITUTE));
   							break;
   						}
   					}
	   			} else {
	   				BeanUtils.setProperty(session_match, entry.getKey(), entry.getValue()[0]);
	   			}
	   		}
			
			for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
	   			if(entry.getKey().contains("_")) {
	   				if(entry.getKey().split("_")[0].equalsIgnoreCase(RugbyUtil.HOME + RugbyUtil.CAPTAIN 
	   						+ RugbyUtil.GOAL_KEEPER.replace("_", ""))) {
	   					for(Player plyr:home_squad) {
	   						if(plyr.getPlayerPosition() == Integer.parseInt(entry.getKey().split("_")[1])) {
	   							plyr.setCaptainGoalKeeper(entry.getValue()[0]);
	   						}
	   					}
	   					for(Player plyr:home_substitutes) {
	   						if(plyr.getPlayerPosition() == Integer.parseInt(entry.getKey().split("_")[1])) {
	   							plyr.setCaptainGoalKeeper(entry.getValue()[0]);
	   						}
	   					}
	   				} else if(entry.getKey().split("_")[0].equalsIgnoreCase(RugbyUtil.AWAY + RugbyUtil.CAPTAIN 
	   						+ RugbyUtil.GOAL_KEEPER.replace("_", ""))) {
	   					for(Player plyr:away_squad) {
	   						if(plyr.getPlayerPosition() == Integer.parseInt(entry.getKey().split("_")[1])) {
	   							plyr.setCaptainGoalKeeper(entry.getValue()[0]);
	   						}
	   					}
	   					for(Player plyr:away_substitutes) {
	   						if(plyr.getPlayerPosition() == Integer.parseInt(entry.getKey().split("_")[1])) {
	   							plyr.setCaptainGoalKeeper(entry.getValue()[0]);
	   						}
	   					}
   					}
	   			}
	   		}

			session_match.setHomeSquad(home_squad);
			session_match.setAwaySquad(away_squad);
			
			Collections.sort(session_match.getHomeSquad());
			Collections.sort(session_match.getAwaySquad());

			session_match.setHomeSubstitutes(home_substitutes);
			session_match.setAwaySubstitutes(away_substitutes);
			
			Collections.sort(session_match.getHomeSubstitutes());
			Collections.sort(session_match.getAwaySubstitutes());
			
			session_match.setHomeOtherSquad(RugbyFunctions.getPlayersFromDB(rugbyService, RugbyUtil.HOME, session_match));
			session_match.setAwayOtherSquad(RugbyFunctions.getPlayersFromDB(rugbyService, RugbyUtil.AWAY, session_match));

			new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()).createNewFile();
			new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.EVENT_DIRECTORY + session_match.getMatchFileName()).createNewFile();
			
			session_match = RugbyFunctions.populateMatchVariables(rugbyService, session_match);

			JAXBContext.newInstance(Match.class).createMarshaller().marshal(session_match, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()));

			JAXBContext.newInstance(EventFile.class).createMarshaller().marshal(session_event, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.EVENT_DIRECTORY + session_match.getMatchFileName()));

		}
		session_match.setEvents(session_event.getEvents());
		return JSONObject.fromObject(session_match).toString();
	}
	
	@RequestMapping(value = {"/processRugbyProcedures"}, method={RequestMethod.GET,RequestMethod.POST})    
	public @ResponseBody String processRugbyProcedures(
			@RequestParam(value = "whatToProcess", required = false, defaultValue = "") String whatToProcess,
			@RequestParam(value = "valueToProcess", required = false, defaultValue = "") String valueToProcess)
					throws JAXBException, IllegalAccessException, InvocationTargetException, IOException, NumberFormatException, InterruptedException, 
						CsvException, SAXException, ParserConfigurationException
	{	
		Event this_event = new Event();
		if(!whatToProcess.equalsIgnoreCase(RugbyUtil.LOAD_TEAMS)) {
			if(valueToProcess.contains(",")) {
				if(session_match.getMatchFileName() == null || session_match.getMatchFileName().isEmpty()) {
					session_match = (Match) JAXBContext.newInstance(Match.class).createUnmarshaller().unmarshal(
							new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.MATCHES_DIRECTORY + valueToProcess.split(",")[0]));
					
					session_event = (EventFile) JAXBContext.newInstance(EventFile.class).createUnmarshaller().unmarshal(
							new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.EVENT_DIRECTORY + valueToProcess.split(",")[0]));
					
					session_match.setEvents(session_event.getEvents());
					session_match = RugbyFunctions.populateMatchVariables(rugbyService,session_match);
				}
			}
		}
		
		switch (whatToProcess.toUpperCase()) {
		case RugbyUtil.LOG_STAT:

			if(valueToProcess.toUpperCase().contains(RugbyUtil.PENALTIES)) {
				if(valueToProcess.split(",")[1].split("_")[1].toUpperCase().contains(RugbyUtil.INCREMENT)) {
					if(valueToProcess.split(",")[1].split("_")[0].toUpperCase().contains(RugbyUtil.HOME)) {
						if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(RugbyUtil.HIT)) {
							session_match.setHomePenaltiesHits(session_match.getHomePenaltiesHits() + 1);
						}else if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(RugbyUtil.MISS)) {
							session_match.setHomePenaltiesMisses(session_match.getHomePenaltiesMisses() + 1);
						}
					}else if(valueToProcess.split(",")[1].split("_")[0].toUpperCase().contains(RugbyUtil.AWAY)) {
						if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(RugbyUtil.HIT)) {
							session_match.setAwayPenaltiesHits(session_match.getAwayPenaltiesHits() + 1);
						}else if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(RugbyUtil.MISS)) {
							session_match.setAwayPenaltiesMisses(session_match.getAwayPenaltiesMisses() + 1);
						}
					}
				}else if(valueToProcess.split(",")[1].split("_")[1].toUpperCase().contains(RugbyUtil.DECREMENT)) {
					if(valueToProcess.split(",")[1].split("_")[0].toUpperCase().contains(RugbyUtil.HOME)) {
						if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(RugbyUtil.HIT)) {
							if(session_match.getHomePenaltiesHits() > 0) {
								session_match.setHomePenaltiesHits(session_match.getHomePenaltiesHits() - 1);
							}
						}else if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(RugbyUtil.MISS)) {
							if(session_match.getHomePenaltiesMisses() > 0) {
								session_match.setHomePenaltiesMisses(session_match.getHomePenaltiesMisses() - 1);
							}
						}
					}else if(valueToProcess.split(",")[1].split("_")[0].toUpperCase().contains(RugbyUtil.AWAY)) {
						if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(RugbyUtil.HIT)) {
							if(session_match.getAwayPenaltiesHits() > 0) {
								session_match.setAwayPenaltiesHits(session_match.getAwayPenaltiesHits() - 1);
							}
						}else if(valueToProcess.split(",")[1].split("_")[3].toUpperCase().contains(RugbyUtil.MISS)) {
							if(session_match.getAwayPenaltiesMisses() > 0) {
								session_match.setAwayPenaltiesMisses(session_match.getAwayPenaltiesMisses() - 1);
							}
						}
					}
				}
			}
			
			JAXBContext.newInstance(Match.class).createMarshaller().marshal(session_match, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()));
			
			switch (session_selected_broadcaster) {
			case RugbyUtil.KHELO_INDIA:
				session_khelo_india.ProcessGraphicOption(whatToProcess,session_match, session_clock,rugbyService,session_socket, session_selected_scenes, valueToProcess);
				break;
			case RugbyUtil.SUPER_CUP:
				session_super_cup.ProcessGraphicOption(whatToProcess, session_match, session_clock, rugbyService, session_socket, session_selected_scenes, valueToProcess);
				break;	
			}
			//session_i_league.ProcessGraphicOption(whatToProcess,session_match, session_clock,rugbyService,session_socket, session_selected_scenes, valueToProcess);
			return JSONObject.fromObject(session_match).toString();
			
		case "RESET_PENALTY":
			
			session_match.setHomePenaltiesHits(0);
			session_match.setHomePenaltiesMisses(0);
			session_match.setAwayPenaltiesHits(0);
			session_match.setAwayPenaltiesMisses(0);

			JAXBContext.newInstance(Match.class).createMarshaller().marshal(session_match, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()));
			
			return JSONObject.fromObject(session_match).toString();
		
		case "NAMESUPER_GRAPHICS-OPTIONS": case "BUG_DB_GRAPHICS-OPTIONS": case "STAFF_GRAPHICS-OPTIONS": case "PROMO_GRAPHICS-OPTIONS": case "LTPROMO_GRAPHICS-OPTIONS":
		case "SCOREBUGPROMO_GRAPHICS-OPTIONS":	case "RESULT_PROMO_GRAPHICS-OPTIONS":
			switch (session_selected_broadcaster) {
			case RugbyUtil.KHELO_INDIA:
				return session_khelo_india.ProcessGraphicOption(whatToProcess,session_match,session_clock, 
						rugbyService,session_socket, session_selected_scenes, valueToProcess).toString();
			case RugbyUtil.SUPER_CUP:
				return session_super_cup.ProcessGraphicOption(whatToProcess, session_match, session_clock, 
						rugbyService, session_socket, session_selected_scenes, valueToProcess).toString();
				
			}
		case "POPULATE-L3-HEATMAP":
			int team_number = 0;
			String player_data = "";
			if(session_match.getHomeTeamId() == Integer.valueOf(valueToProcess.split(",")[2])) {
				team_number = 0;
			}else if(session_match.getAwayTeamId() == Integer.valueOf(valueToProcess.split(",")[2])){
				team_number = 1;
			}
			
			switch(valueToProcess.split(",")[3].toUpperCase()) {
			case "HEATMAP":
				player_data = "playerheatmap" + team_number + "_" + rugbyService.getAllPlayer().get(Integer.valueOf(valueToProcess.split(",")[4]) - 1).getJersey_number();
				
				break;
			case "PEAKDISTANCE":
				player_data = "playerpeakdistancegraph" + team_number + "_" + rugbyService.getAllPlayer().get(Integer.valueOf(valueToProcess.split(",")[4]) - 1).getJersey_number();
				break;
			}
			session_match.setApi_photo(RugbyFunctions.FTPImageDownload(21, 0, "ISL-Rise", "jPRka3xH3t!43Sr89K", player_data));
			
			switch (session_selected_broadcaster) {
			case RugbyUtil.SUPER_CUP:
				session_super_cup.ProcessGraphicOption(whatToProcess, session_match, session_clock, rugbyService, session_socket, 
						session_selected_scenes, valueToProcess);
				break;
			}
			
			return JSONObject.fromObject(session_match).toString();
		case "APIDATA_GRAPHICS-OPTIONS":
			
			try {
		         URL url = new URL(RugbyUtil.API_PATH1 + session_match.getMatchId()+ RugbyUtil.API_PATH2);
		         URLConnection connection = url.openConnection();
		         connection.connect();
		         LiveMatchData my_data = new ObjectMapper().readValue(url, LiveMatchData.class);
					
					if(my_data.getTeamShortMatchStats().getTeam_stats_data().size() > 0) {
						for(int i = 0; i <= my_data.getTeamShortMatchStats().getTeam_stats_data().size() -1; i++ ) {
							session_match.setApiData(my_data.getTeamShortMatchStats().getTeam_stats_data());
						}
					}
		      } catch (MalformedURLException e) {
		         System.out.println("Internet is not connected");
		      } catch (IOException e) {
		         System.out.println("Internet is not connected");
		      }
			return JSONObject.fromObject(session_match).toString();
			
		case RugbyUtil.REPLACE:
			
			Player store_player = new Player();
			for(int i=0 ; i<= session_match.getHomeSquad().size()-1;i++) {
				
				if(session_match.getHomeSquad().get(i).getPlayerId() == Integer.valueOf(valueToProcess.split(",")[1])) {
					store_player = session_match.getHomeSquad().get(i);
					session_match.getHomeSquad().remove(i);
					for(int j=0 ; j<= session_match.getHomeSubstitutes().size()-1;j++) {
						if(session_match.getHomeSubstitutes().get(j).getPlayerId() == Integer.valueOf(valueToProcess.split(",")[2])) {
							session_match.getHomeSquad().add(i, session_match.getHomeSubstitutes().get(j));
							session_match.getHomeSubstitutes().remove(j);
							session_match.getHomeSubstitutes().add(j, store_player);
						}
					}
				}
			}
			for(int i=0 ; i<= session_match.getAwaySquad().size()-1;i++) {
				
				if(session_match.getAwaySquad().get(i).getPlayerId() == Integer.valueOf(valueToProcess.split(",")[1])) {
					store_player = session_match.getAwaySquad().get(i);
					session_match.getAwaySquad().remove(i);
					for(int j=0 ; j<= session_match.getAwaySubstitutes().size()-1;j++) {
						if(session_match.getAwaySubstitutes().get(j).getPlayerId() == Integer.valueOf(valueToProcess.split(",")[2])) {
							session_match.getAwaySquad().add(i, session_match.getAwaySubstitutes().get(j));
							session_match.getAwaySubstitutes().remove(j);
							session_match.getAwaySubstitutes().add(j, store_player);
						}
					}
				}
			}
			
			session_event.getEvents().add(new Event(session_event.getEvents().size() + 1, 0, session_match.getClock().getMatchHalves(), 
					0,whatToProcess, "replace", Integer.valueOf(valueToProcess.split(",")[1]),Integer.valueOf(valueToProcess.split(",")[2]),0));
			
			JAXBContext.newInstance(EventFile.class).createMarshaller().marshal(session_event, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.EVENT_DIRECTORY + session_match.getMatchFileName()));
			
			JAXBContext.newInstance(Match.class).createMarshaller().marshal(session_match, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()));
			
			session_match.setEvents(session_event.getEvents());
			
			return JSONObject.fromObject(session_match).toString();
				
		case RugbyUtil.LOG_EVENT:
			
			if(!valueToProcess.trim().isEmpty() && valueToProcess.contains(",") == true) {
				
				if(session_match.getMatchStats() == null || session_match.getMatchStats().size() <= 0) 
					session_match.setMatchStats(new ArrayList<MatchStats>());
				if(session_match.getEvents() == null || session_match.getEvents().size() <= 0) 
					session_match.setEvents(new ArrayList<Event>());
				
				switch (valueToProcess.split(",")[1].toUpperCase()) {
				case RugbyUtil.GOAL: case RugbyUtil.OWN_GOAL: case RugbyUtil.PENALTY: case RugbyUtil.YELLOW: case RugbyUtil.RED:
				case RugbyUtil.SHOTS_ON_TARGET: case RugbyUtil.SHOTS: case RugbyUtil.CORNERS_CONVERTED: case RugbyUtil.CORNERS:
				case RugbyUtil.ASSISTS: case RugbyUtil.OFF_SIDE: case RugbyUtil.FOULS:
					
					session_match.getMatchStats().add(new MatchStats(session_match.getMatchStats().size() + 1, Integer.valueOf(valueToProcess.split(",")[2]), 
							session_match.getClock().getMatchHalves(),valueToProcess.split(",")[1], 1, (session_match.getClock().getMatchTotalMilliSeconds()/1000)));
					
					for(Player plyr : session_match.getHomeSquad()) {
						if(plyr.getPlayerId() == Integer.valueOf(valueToProcess.split(",")[2])) {
							switch (valueToProcess.split(",")[1].toUpperCase()) {
							case RugbyUtil.GOAL: case RugbyUtil.PENALTY:
								session_match.setHomeTeamScore(session_match.getHomeTeamScore() + 1);
								break;
							case RugbyUtil.OWN_GOAL: 
								session_match.setAwayTeamScore(session_match.getAwayTeamScore() + 1);
								break;
							}
						}
					}
					for(Player plyr : session_match.getAwaySquad()) {
						if(plyr.getPlayerId() == Integer.valueOf(valueToProcess.split(",")[2])) {
							switch (valueToProcess.split(",")[1].toUpperCase()) {
							case RugbyUtil.GOAL: case RugbyUtil.PENALTY:
								session_match.setAwayTeamScore(session_match.getAwayTeamScore() + 1);
								break;
							case RugbyUtil.OWN_GOAL: 
								session_match.setHomeTeamScore(session_match.getHomeTeamScore() + 1);
								break;
							}
						}
					}
					break;
				}

				if(session_event.getEvents() == null || session_event.getEvents().size() <= 0) 
					session_event.setEvents(new ArrayList<Event>());
				
				session_event.getEvents().add(new Event(session_event.getEvents().size() + 1, Integer.valueOf(valueToProcess.split(",")[2]), 
						session_match.getClock().getMatchHalves(), session_match.getMatchStats().size(),whatToProcess, valueToProcess.split(",")[1], 0,0,1));
				
			}

			session_match = RugbyFunctions.populateMatchVariables(rugbyService, session_match);
			
			JAXBContext.newInstance(EventFile.class).createMarshaller().marshal(session_event, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.EVENT_DIRECTORY + session_match.getMatchFileName()));
			
			JAXBContext.newInstance(Match.class).createMarshaller().marshal(session_match, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()));
			
			session_match.setEvents(session_event.getEvents());
			
			return JSONObject.fromObject(session_match).toString();

		case "HOME_GOAL":
			
			if(session_match.getMatchStats() == null || session_match.getMatchStats().size() <= 0) 
				session_match.setMatchStats(new ArrayList<MatchStats>());
			if(session_match.getEvents() == null || session_match.getEvents().size() <= 0) 
				session_match.setEvents(new ArrayList<Event>());
			
			session_match.getMatchStats().add(new MatchStats(session_match.getMatchStats().size() + 1, session_match.getHomeSquad().get(0).getPlayerId(), 
					session_match.getClock().getMatchHalves(),"Home_Goal", 1, (session_match.getClock().getMatchTotalMilliSeconds()/1000)));
			
			session_match.setHomeTeamScore(session_match.getHomeTeamScore() + 1);
			
			if(session_event.getEvents() == null || session_event.getEvents().size() <= 0) 
				session_event.setEvents(new ArrayList<Event>());
			
			session_event.getEvents().add(new Event(session_event.getEvents().size() + 1, session_match.getHomeSquad().get(0).getPlayerId(), 
					session_match.getClock().getMatchHalves(), session_match.getMatchStats().size(),whatToProcess, "Home_Goal", 0,0,1));

			session_match = RugbyFunctions.populateMatchVariables(rugbyService, session_match);
			
			JAXBContext.newInstance(EventFile.class).createMarshaller().marshal(session_event, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.EVENT_DIRECTORY + session_match.getMatchFileName()));
			
			JAXBContext.newInstance(Match.class).createMarshaller().marshal(session_match, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()));
			
			session_match.setEvents(session_event.getEvents());
			
			return JSONObject.fromObject(session_match).toString();
			
		case "AWAY_GOAL":
			
			if(session_match.getMatchStats() == null || session_match.getMatchStats().size() <= 0) 
				session_match.setMatchStats(new ArrayList<MatchStats>());
			if(session_match.getEvents() == null || session_match.getEvents().size() <= 0) 
				session_match.setEvents(new ArrayList<Event>());
			
			session_match.getMatchStats().add(new MatchStats(session_match.getMatchStats().size() + 1, session_match.getAwaySquad().get(0).getPlayerId(), 
					session_match.getClock().getMatchHalves(),"AWAY_GOAL", 1, (session_match.getClock().getMatchTotalMilliSeconds()/1000)));
			
			session_match.setAwayTeamScore(session_match.getAwayTeamScore() + 1);
			
			if(session_event.getEvents() == null || session_event.getEvents().size() <= 0) 
				session_event.setEvents(new ArrayList<Event>());
			
			session_event.getEvents().add(new Event(session_event.getEvents().size() + 1, session_match.getAwaySquad().get(0).getPlayerId(), 
					session_match.getClock().getMatchHalves(), session_match.getMatchStats().size(),whatToProcess, "AWAY_GOAL", 0,0,1));

			session_match = RugbyFunctions.populateMatchVariables(rugbyService, session_match);
			
			JAXBContext.newInstance(EventFile.class).createMarshaller().marshal(session_event, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.EVENT_DIRECTORY + session_match.getMatchFileName()));
			
			JAXBContext.newInstance(Match.class).createMarshaller().marshal(session_match, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()));
		
			session_match.setEvents(session_event.getEvents());
			
			return JSONObject.fromObject(session_match).toString();	

		case "LOG_OVERWRITE_MATCH_SUBS":
			//System.out.println(valueToProcess);
			if(valueToProcess.contains(",")) {
				int overwrite_palyer_off_id = 0,overwrite_palyer_on_id = 0;
				Player sub_store_player = new Player();
				if(session_event.getEvents() != null) {
					for(Event evnt : session_event.getEvents()) {
						if(evnt.getEventNumber() == Integer.valueOf(valueToProcess.split(",")[1])) {
							if(Integer.valueOf(valueToProcess.split(",")[3]) > 0 && Integer.valueOf(valueToProcess.split(",")[2]) == 0) {
								overwrite_palyer_off_id = evnt.getOnPlayerId();
								overwrite_palyer_on_id = Integer.valueOf(valueToProcess.split(",")[3]);
								
								evnt.setOnPlayerId(overwrite_palyer_on_id);
								
							}else if(Integer.valueOf(valueToProcess.split(",")[3]) == 0 && Integer.valueOf(valueToProcess.split(",")[2]) > 0) {
								overwrite_palyer_off_id = Integer.valueOf(valueToProcess.split(",")[2]);
								overwrite_palyer_on_id = evnt.getOffPlayerId();
								
								evnt.setOffPlayerId(overwrite_palyer_off_id);
								
							}else if(Integer.valueOf(valueToProcess.split(",")[3]) > 0 && Integer.valueOf(valueToProcess.split(",")[2]) > 0) {
								overwrite_palyer_off_id = Integer.valueOf(valueToProcess.split(",")[2]);
								overwrite_palyer_on_id = Integer.valueOf(valueToProcess.split(",")[3]);
								
								evnt.setOnPlayerId(overwrite_palyer_on_id);
								evnt.setOffPlayerId(overwrite_palyer_off_id);
								
							}
							
						}
					}
				}
				//System.out.println("ON - " + overwrite_palyer_on_id + " OFF - " + overwrite_palyer_off_id);
				for(int i=0 ; i<= session_match.getHomeSquad().size()-1;i++) {
					if(session_match.getHomeSquad().get(i).getPlayerId() == overwrite_palyer_off_id) {
						sub_store_player = session_match.getHomeSquad().get(i);
						session_match.getHomeSquad().remove(i);
						for(int j=0 ; j<= session_match.getHomeSubstitutes().size()-1;j++) {
							if(session_match.getHomeSubstitutes().get(j).getPlayerId() == overwrite_palyer_on_id) {
								session_match.getHomeSquad().add(i, session_match.getHomeSubstitutes().get(j));
								session_match.getHomeSubstitutes().remove(j);
								session_match.getHomeSubstitutes().add(j, sub_store_player);
							}
						}
					}
				}
				for(int i=0 ; i<= session_match.getAwaySquad().size()-1;i++) {
					if(session_match.getAwaySquad().get(i).getPlayerId() == overwrite_palyer_off_id) {
						sub_store_player = session_match.getAwaySquad().get(i);
						session_match.getAwaySquad().remove(i);
						for(int j=0 ; j<= session_match.getAwaySubstitutes().size()-1;j++) {
							if(session_match.getAwaySubstitutes().get(j).getPlayerId() == overwrite_palyer_on_id) {
								session_match.getAwaySquad().add(i, session_match.getAwaySubstitutes().get(j));
								session_match.getAwaySubstitutes().remove(j);
								session_match.getAwaySubstitutes().add(j, sub_store_player);
							}
						}
					}
				}
			}
			
			JAXBContext.newInstance(EventFile.class).createMarshaller().marshal(session_event, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.EVENT_DIRECTORY + session_match.getMatchFileName()));
			
			JAXBContext.newInstance(Match.class).createMarshaller().marshal(session_match, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()));
			return JSONObject.fromObject(session_match).toString();
		case "LOG_OVERWRITE_MATCH_STATS":
		
			if(valueToProcess.contains(",")) {
				if(session_match.getMatchStats() != null) {
					for(MatchStats ms : session_match.getMatchStats()) {
						if(ms.getStatsId() == Integer.valueOf(valueToProcess.split(",")[1])) {
							ms.setPlayerId(Integer.valueOf(valueToProcess.split(",")[2]));
							ms.setStats_type(valueToProcess.split(",")[3]);
							ms.setTotalMatchSeconds(Long.valueOf(valueToProcess.split(",")[4]));
						}
					}
				}
				if(session_event.getEvents() != null) {
					for(Event evnt : session_event.getEvents()) {
						if(evnt.getStatsId() == Integer.valueOf(valueToProcess.split(",")[1])) {
							evnt.setEventPlayerId(Integer.valueOf(valueToProcess.split(",")[2]));
							evnt.setEventLog("LOG_EVENT");
							evnt.setEventType(valueToProcess.split(",")[3]);
						}
					}
				}
			}

			JAXBContext.newInstance(Match.class).createMarshaller().marshal(session_match, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()));
			
			JAXBContext.newInstance(EventFile.class).createMarshaller().marshal(session_event, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.EVENT_DIRECTORY + session_match.getMatchFileName()));
			
			session_match = RugbyFunctions.populateMatchVariables(rugbyService, session_match);
			session_match.setEvents(session_event.getEvents());

			return JSONObject.fromObject(session_match).toString();
		
		case RugbyUtil.LOG_OVERWRITE_TEAM_SCORE: 
			
			session_match.setHomeTeamScore(Integer.valueOf(valueToProcess.split(",")[1]));
			session_match.setAwayTeamScore(Integer.valueOf(valueToProcess.split(",")[2]));

			JAXBContext.newInstance(Match.class).createMarshaller().marshal(session_match, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()));

			return JSONObject.fromObject(session_match).toString();
		
		case "HOME_UNDO":
		
			if(session_event.getEvents() != null && 1 <= session_event.getEvents().size()) {
				
				for(int jUndo=1;jUndo<=1;jUndo++) {

					this_event = session_event.getEvents().get(session_event.getEvents().size() - 1);
					switch (this_event.getEventLog().toUpperCase()) {
					case "HOME_GOAL":
						switch (this_event.getEventType().toUpperCase()) {
						case "HOME_GOAL":
							this_event = session_event.getEvents().get(session_event.getEvents().size() - 1);
							for(Player plyr : session_match.getHomeSquad()) {
								if(plyr.getPlayerId() == this_event.getEventPlayerId()) {
									switch (this_event.getEventType().toUpperCase()) {
									case "HOME_GOAL":
										session_match.setHomeTeamScore(session_match.getHomeTeamScore() - 1);
										session_event.getEvents().remove(this_event);
										session_match.getMatchStats().remove(session_match.getMatchStats().get(session_match.getMatchStats().size() - 1));
										break;
									}
								}
							}
							for(Player plyr : session_match.getAwaySquad()) {
								if(plyr.getPlayerId() == this_event.getEventPlayerId()) {
									switch (this_event.getEventType().toUpperCase()) {
									case "HOME_GOAL":
										session_match.setAwayTeamScore(session_match.getAwayTeamScore() - 1);
										session_event.getEvents().remove(this_event);
										session_match.getMatchStats().remove(session_match.getMatchStats().get(session_match.getMatchStats().size() - 1));
										break;
									}
								}
							}
							
							break;
						}
						break;
					}
				}
			}
			
	
			JAXBContext.newInstance(EventFile.class).createMarshaller().marshal(session_event, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.EVENT_DIRECTORY + session_match.getMatchFileName()));
			
			JAXBContext.newInstance(Match.class).createMarshaller().marshal(session_match, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()));
		
			session_match.setEvents(session_event.getEvents());
			return JSONObject.fromObject(session_match).toString();
			
		case "AWAY_UNDO":

			if(session_event.getEvents() != null && 1 <= session_event.getEvents().size()) {
				
				for(int jUndo=1;jUndo<=1;jUndo++) {

					this_event = session_event.getEvents().get(session_event.getEvents().size() - 1);
					
					switch (this_event.getEventLog().toUpperCase()) {
					case "AWAY_GOAL":
						switch (this_event.getEventType().toUpperCase()) {
						case "AWAY_GOAL":
							this_event = session_event.getEvents().get(session_event.getEvents().size() - 1);
							for(Player plyr : session_match.getHomeSquad()) {
								if(plyr.getPlayerId() == this_event.getEventPlayerId()) {
									switch (this_event.getEventType().toUpperCase()) {
									case "AWAY_GOAL":
										session_match.setHomeTeamScore(session_match.getHomeTeamScore() - 1);
										session_event.getEvents().remove(this_event);
										session_match.getMatchStats().remove(session_match.getMatchStats().get(session_match.getMatchStats().size() - 1));
										break;
									}
								}
							}
							for(Player plyr : session_match.getAwaySquad()) {
								if(plyr.getPlayerId() == this_event.getEventPlayerId()) {
									switch (this_event.getEventType().toUpperCase()) {
									case "AWAY_GOAL":
										session_match.setAwayTeamScore(session_match.getAwayTeamScore() - 1);
										session_event.getEvents().remove(this_event);
										session_match.getMatchStats().remove(session_match.getMatchStats().get(session_match.getMatchStats().size() - 1));
										break;
									}
								}
							}
							break;
						}
						break;
					}
				}
			}
			
	
			JAXBContext.newInstance(EventFile.class).createMarshaller().marshal(session_event, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.EVENT_DIRECTORY + session_match.getMatchFileName()));
			
			JAXBContext.newInstance(Match.class).createMarshaller().marshal(session_match, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()));
		
			session_match.setEvents(session_event.getEvents());
			return JSONObject.fromObject(session_match).toString();	
			
		case RugbyUtil.UNDO:

			if(valueToProcess.contains(",")) {
				if(session_event.getEvents() != null && Integer.valueOf(valueToProcess.split(",")[1]) <= session_event.getEvents().size()) {
					for(int iUndo=1;iUndo<=Integer.valueOf(valueToProcess.split(",")[1]);iUndo++) {

						this_event = session_event.getEvents().get(session_event.getEvents().size() - 1);
						
						switch (this_event.getEventLog().toUpperCase()) {
						case RugbyUtil.LOG_EVENT:
							switch (this_event.getEventType().toUpperCase()) {
							case RugbyUtil.GOAL: case RugbyUtil.OWN_GOAL: case RugbyUtil.PENALTY: case RugbyUtil.YELLOW: case RugbyUtil.RED:
							case RugbyUtil.ASSISTS: case RugbyUtil.SHOTS: case RugbyUtil.SHOTS_ON_TARGET: case RugbyUtil.OFF_SIDE: case RugbyUtil.FOULS:
							case RugbyUtil.CORNERS_CONVERTED: case RugbyUtil.CORNERS: case RugbyUtil.SAVES:
								this_event = session_event.getEvents().get(session_event.getEvents().size() - 1);
								session_match.getMatchStats().remove(session_match.getMatchStats().get(session_match.getMatchStats().size() - 1));
								for(Player plyr : session_match.getHomeSquad()) {
									if(plyr.getPlayerId() == this_event.getEventPlayerId()) {
										switch (this_event.getEventType().toUpperCase()) {
										case RugbyUtil.GOAL: case RugbyUtil.PENALTY:
											session_match.setHomeTeamScore(session_match.getHomeTeamScore() - 1);
											break;
										case RugbyUtil.OWN_GOAL: 
											session_match.setAwayTeamScore(session_match.getAwayTeamScore() - 1);
											break;
										}
									}
								}
								for(Player plyr : session_match.getAwaySquad()) {
									if(plyr.getPlayerId() == this_event.getEventPlayerId()) {
										switch (this_event.getEventType().toUpperCase()) {
										case RugbyUtil.GOAL: case RugbyUtil.PENALTY:
											session_match.setAwayTeamScore(session_match.getAwayTeamScore() - 1);
											break;
										case RugbyUtil.OWN_GOAL: 
											session_match.setHomeTeamScore(session_match.getHomeTeamScore() - 1);
											break;
										}
									}
								}
								break;
							}
							break;
						case RugbyUtil.REPLACE:
							ArrayList<Player> undo_store_player = new ArrayList<Player>();
							for(int i=0 ; i<= session_match.getHomeSquad().size()-1;i++) {
								if(session_match.getHomeSquad().get(i).getPlayerId() == this_event.getOnPlayerId()) {
									undo_store_player.add(session_match.getHomeSquad().get(i));
									session_match.getHomeSquad().remove(i);
									for(int j=0 ; j<= session_match.getHomeSubstitutes().size()-1;j++) {
										if(session_match.getHomeSubstitutes().get(j).getPlayerId() == this_event.getOffPlayerId()) {
											session_match.getHomeSquad().add(i, session_match.getHomeSubstitutes().get(j));
											session_match.getHomeSubstitutes().remove(j);
											session_match.getHomeSubstitutes().add(j, undo_store_player.get(0));
											undo_store_player.remove(0);
										}
									}
								}
							}
							for(int i=0 ; i<= session_match.getAwaySquad().size()-1;i++) {
								if(session_match.getAwaySquad().get(i).getPlayerId() == this_event.getOnPlayerId()) {
									undo_store_player.add(session_match.getAwaySquad().get(i));
									session_match.getAwaySquad().remove(i);
									for(int j=0 ; j<= session_match.getAwaySubstitutes().size()-1;j++) {
										if(session_match.getAwaySubstitutes().get(j).getPlayerId() == this_event.getOffPlayerId()) {
											session_match.getAwaySquad().add(i, session_match.getAwaySubstitutes().get(j));
											session_match.getAwaySubstitutes().remove(j);
											session_match.getAwaySubstitutes().add(j, undo_store_player.get(0));
											undo_store_player.remove(0);
										}
									}
								}
							}
							break;
						}
						session_event.getEvents().remove(this_event);
					}
				}
			}
			
			
			JAXBContext.newInstance(EventFile.class).createMarshaller().marshal(session_event, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.EVENT_DIRECTORY + session_match.getMatchFileName()));
			
			JAXBContext.newInstance(Match.class).createMarshaller().marshal(session_match, 
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()));

			session_match.setEvents(session_event.getEvents());
			return JSONObject.fromObject(session_match).toString();
			
		case RugbyUtil.LOAD_TEAMS:
			if(!valueToProcess.trim().isEmpty()) {
				
				session_match.setHomeTeam(rugbyService.getTeam(RugbyUtil.TEAM, valueToProcess.split(",")[0]));
				session_match.setAwayTeam(rugbyService.getTeam(RugbyUtil.TEAM, valueToProcess.split(",")[1]));
				
				session_match.setHomeSquad(rugbyService.getPlayers(RugbyUtil.TEAM, valueToProcess.split(",")[0]));
				session_match.setAwaySquad(rugbyService.getPlayers(RugbyUtil.TEAM, valueToProcess.split(",")[1]));
			}
			
			return JSONObject.fromObject(session_match).toString();

		case RugbyUtil.LOAD_MATCH: case RugbyUtil.LOAD_SETUP:

			session_match = (Match) JAXBContext.newInstance(Match.class).createUnmarshaller().unmarshal(
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.MATCHES_DIRECTORY + valueToProcess));
			
			switch (whatToProcess.toUpperCase()) {
			case RugbyUtil.LOAD_MATCH:
				
				if(new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.EVENT_DIRECTORY + valueToProcess).exists()) {
					session_event = (EventFile) JAXBContext.newInstance(EventFile.class).createUnmarshaller().unmarshal(
							new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.EVENT_DIRECTORY + valueToProcess));
				} else {
					session_event = new EventFile();
					new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.EVENT_DIRECTORY + valueToProcess).createNewFile();
				}

				if(new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.CLOCK_XML).exists()) {
					session_match.setClock((Clock) JAXBContext.newInstance(Clock.class).createUnmarshaller().unmarshal(
							new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.CLOCK_XML)));
				} else {
					session_match.setClock(new Clock());
				}
				break;
				
			}
			
			switch (whatToProcess.toUpperCase()) {
			case RugbyUtil.LOAD_SETUP:
				session_match.setHomeOtherSquad(RugbyFunctions.getPlayersFromDB(rugbyService, RugbyUtil.HOME, session_match));
				session_match.setAwayOtherSquad(RugbyFunctions.getPlayersFromDB(rugbyService, RugbyUtil.AWAY, session_match));
				break;
			}
			session_match = RugbyFunctions.populateMatchVariables(rugbyService,session_match);

			session_match.setEvents(session_event.getEvents());

			return JSONObject.fromObject(session_match).toString();			

		case "READ_CLOCK":
			if(new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.CLOCK_XML).exists()) {
				session_clock = (Clock) JAXBContext.newInstance(Clock.class).createUnmarshaller().unmarshal(
						new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.CLOCK_XML));
				session_match.setClock(session_clock);
				
				switch (session_selected_broadcaster) {
				case RugbyUtil.KHELO_INDIA:
					session_khelo_india.updateScoreBug(session_selected_scenes,session_match, session_socket);
					break;
				case RugbyUtil.SUPER_CUP:
					session_super_cup.updateScoreBug(session_selected_scenes, session_match,rugbyService, session_socket);
					break;	
				}
			}
			return JSONObject.fromObject(session_match).toString();
						
		default:
			switch (session_selected_broadcaster) {
			case RugbyUtil.KHELO_INDIA:
				session_khelo_india.ProcessGraphicOption(whatToProcess,session_match, session_clock,rugbyService,session_socket, 
						session_selected_scenes, valueToProcess);
				break;
			case RugbyUtil.SUPER_CUP:
				session_super_cup.ProcessGraphicOption(whatToProcess, session_match, session_clock, rugbyService, session_socket, 
						session_selected_scenes, valueToProcess);
				break;	
			}
			return JSONObject.fromObject(session_match).toString();
		}
	}
}
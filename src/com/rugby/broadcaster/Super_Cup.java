package com.rugby.broadcaster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import javax.xml.parsers.*;

import com.rugby.model.*;
import com.rugby.service.RugbyService;
import com.rugby.util.RugbyFunctions;
import com.rugby.util.RugbyUtil;

import com.opencsv.exceptions.CsvException;

import net.sf.json.JSONArray;

import com.rugby.containers.Scene;
import com.rugby.containers.ScoreBug;

public class Super_Cup extends Scene{
	
	public String session_selected_broadcaster = "VIZ_TRI_NATION";
	
	public PrintWriter print_writer;
	public ScoreBug scorebug = new ScoreBug(); 
	public String which_graphics_onscreen = "";
	public boolean is_infobar = false;
	public String logo_path = "IMAGE*/Default/Essentials/Badges/";
	public String logo_bw_path = "IMAGE*/Default/Essentials/BadgesBW/";
	public String logo_outline_path = "IMAGE*/Default/Essentials/BadgesOutline/";
	public String logo2_path = "IMAGE*/Default/Design/";
	private String colors_path = "C:\\Images\\Super_Cup\\Colours\\";
	private String photos_path = "C:\\Images\\Super_Cup\\Photos\\";
	private String image_path = "C:\\Sports\\Football\\Statistic\\Match_Data\\";
	private String status;
	private String slashOrDash = "-";
	public static List<String> penalties;
	public static List<String> penaltiesremove;
	public int which_side = 1;
	
	
	public Super_Cup() {
		super();
	}
	
	public ScoreBug updateScoreBug(List<Scene> scenes, Match match,RugbyService rugbyService, Socket session_socket) throws InterruptedException, MalformedURLException, IOException, CsvException
	{
		if(scorebug.isScorebug_on_screen() == true) {
			scorebug = populateScoreBug(true,scorebug, session_socket, scenes.get(0).getScene_path(),match, session_selected_broadcaster);
			scorebug = populateExtraTime(true,scorebug,session_socket,null,match,session_selected_broadcaster);
		}
		return scorebug;
	}
	public Object ProcessGraphicOption(String whatToProcess,Match match,Clock clock, RugbyService rugbyService,Socket session_socket,
			List<Scene> scenes, String valueToProcess) throws InterruptedException, NumberFormatException, MalformedURLException, IOException, CsvException, JAXBException, SAXException, ParserConfigurationException{
		
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		
		if (which_graphics_onscreen == "PENALTY")
		{
			int iHomeCont = 0, iAwayCont = 0;
			penalties.add(valueToProcess.split(",")[1]);
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$MainScorePart$Seperator$AllScoreGrp$txt_HomeScore*GEOM*TEXT SET " + match.getHomePenaltiesHits() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$MainScorePart$Seperator$AllScoreGrp$txt_AwayScore*GEOM*TEXT SET " + match.getAwayPenaltiesHits() + "\0");
			
			for(String pen : penalties)
			{	
				if(pen.toUpperCase().contains(RugbyUtil.HOME + "_" + RugbyUtil.INCREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.HIT)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "1" + "\0");
				}else if(pen.toUpperCase().contains(RugbyUtil.HOME + "_" + RugbyUtil.INCREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.MISS)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "2" + "\0");
				}else if(pen.toUpperCase().contains(RugbyUtil.AWAY + "_" + RugbyUtil.INCREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.HIT)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "1" + "\0");
				}else if(pen.toUpperCase().contains(RugbyUtil.AWAY + "_" + RugbyUtil.INCREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.MISS)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "2" + "\0");
				}else if(pen.toUpperCase().contains(RugbyUtil.HOME + "_" + RugbyUtil.DECREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.HIT)) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0");
					
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(RugbyUtil.HOME + "_" + RugbyUtil.DECREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.MISS)) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0");

					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(RugbyUtil.AWAY + "_" + RugbyUtil.DECREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.HIT)) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0");

					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}else if(pen.toUpperCase().contains(RugbyUtil.AWAY + "_" + RugbyUtil.DECREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.MISS)) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0");

					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}
			}
			if(match.getHomePenaltiesHits() == 0 && match.getAwayPenaltiesHits() == 0) {
				penalties = new ArrayList<String>();
				penaltiesremove = new ArrayList<String>();
			}
		} else {
			if(penalties == null) {
				penalties = new ArrayList<String>();
				penaltiesremove = new ArrayList<String>();
			}
			if(match.getHomePenaltiesHits() == 0 && match.getAwayPenaltiesHits() == 0) {
				penalties = new ArrayList<String>();
				penaltiesremove = new ArrayList<String>();
			}
			int iHomeCont = 0, iAwayCont = 0;
			penalties.add(valueToProcess.split(",")[1]);
			if((match.getHomePenaltiesHits()+match.getHomePenaltiesMisses()) != 0 && (match.getAwayPenaltiesHits()+match.getAwayPenaltiesHits()) != 0) {
				if(((match.getHomePenaltiesHits()+match.getHomePenaltiesMisses())%5) == 0 && ((match.getAwayPenaltiesHits()+match.getAwayPenaltiesMisses())%5) == 0) {
					if(match.getHomePenaltiesHits() == match.getAwayPenaltiesHits()) {
						penalties = new ArrayList<String>();
//						for(int p=1;p<=5;p++) {
//							print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + p + "$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0");
//							print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + p + "$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0");
//						}
					}
				}
			}
			
			//print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore " + match.getHomePenaltiesHits() + "-" + match.getAwayPenaltiesHits() + ";");

			for(String pen : penalties)
			{
				//System.out.println("ELSE LOOP - " + iHomeCont);
				if(pen.toUpperCase().contains(RugbyUtil.HOME + "_" + RugbyUtil.INCREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.HIT)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "1" + "\0");
				}else if(pen.toUpperCase().contains(RugbyUtil.HOME + "_" + RugbyUtil.INCREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.MISS)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "2" + "\0");
				}else if(pen.toUpperCase().contains(RugbyUtil.AWAY + "_" + RugbyUtil.INCREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.HIT)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "1" + "\0");
				}else if(pen.toUpperCase().contains(RugbyUtil.AWAY + "_" + RugbyUtil.INCREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.MISS)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "2" + "\0");
				}else if(pen.toUpperCase().contains(RugbyUtil.HOME + "_" + RugbyUtil.DECREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.HIT)) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0");

					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
					
				}else if(pen.toUpperCase().contains(RugbyUtil.HOME + "_" + RugbyUtil.DECREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.MISS)) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0");

					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(RugbyUtil.AWAY + "_" + RugbyUtil.DECREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.HIT)) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0");
			
					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}else if(pen.toUpperCase().contains(RugbyUtil.AWAY + "_" + RugbyUtil.DECREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.MISS)) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0");
					
					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}
			}
		}
		
		switch (whatToProcess.toUpperCase()) {
		case "POPULATE-SCOREBUG": case "POPULATE-SCOREBUG_STATS": case "POPULATE-EXTRA_TIME": case "POPULATE-EXTRA_TIME_BOTH": case "POPULATE-RED_CARD": case "POPULATE-EXTRA_TIME_HALF":
		case "POPULATE-SCOREBUG-CARD": case "POPULATE-SCOREBUG-SUBS": case "POPULATE-SUBS_CHANGE_ON": case "POPULATE-SCOREBUG-PROMO":
		case "POPULATE-FF-MATCHID": case "POPULATE-FF-PROMO": case "POPULATE-L3-MATCHSTATUS": case "POPULATE-FF-PLAYINGXI": case "POPULATE-HOMESUB": case "POPULATE-AWAYXI": case "POPULATE-AWAYSUB": 
		case "POPULATE-FF-MATCHSTATS": case "POPULATE-DOUBLE_PROMO": case "POPULATE-FF-TEAMS": case "POPULATE-POINTS_TABLE": case "POPULATE-FIXTURES": case "POPULATE-POINTS_TABLE2":
		case "POPULATE-QUAIFIERS": case "POPULATE-LT-PROMO": case "POPULATE-PLAYOFFS": case "POPULATE-ROAD-TO-FINAL":
			
		case "POPULATE-L3-SCOREUPDATE": case "POPULATE-LT-MATCHID": case "POPULATE-L3-NAMESUPER": case "POPULATE-L3-NAMESUPER-PLAYER": case "POPULATE-L3-NAMESUPER-CARD":
		case "POPULATE-L3-SUBSTITUTE": case "POPULATE-OFFICIALS": case "POPULATE-L3-HEATMAP": case "POPULATE-L3-TOP_STATS": case "POPULATE-L3-STAFF": case "POPULATE-PENALTY":
		case "POPULATE-CHANGE_PENALTY": case "POPULATE-LT-RESULT":
			switch(whatToProcess.toUpperCase()) {
			case "POPULATE-SCOREBUG_STATS": case "POPULATE-EXTRA_TIME": case "POPULATE-EXTRA_TIME_BOTH": case "POPULATE-RED_CARD": case "POPULATE-SCOREBUG-CARD":
			case "POPULATE-SCOREBUG-SUBS": case "POPULATE-SUBS_CHANGE_ON": case "POPULATE-EXTRA_TIME_HALF": case "POPULATE-SCOREBUG-PROMO":
			case "POPULATE-HOMESUB": case "POPULATE-AWAYXI": case "POPULATE-AWAYSUB":
			case "POPULATE-CHANGE_PENALTY":
				break;
			case "POPULATE-SCOREBUG":
				scenes.get(0).scene_load(session_socket, session_selected_broadcaster);
				break;
			case "POPULATE-FF-PLAYINGXI":
				scenes.get(1).setScene_path(valueToProcess.split(",")[1]);
				scenes.get(1).scene_load(session_socket,session_selected_broadcaster);
				print_writer.println("-1 RENDERER*STAGE SHOW 0.0\0");
				print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Reset START \0");
				break;
			case "POPULATE-FF-MATCHID": case "POPULATE-PLAYOFFS":
				if(which_graphics_onscreen == "MATCHID" && whatToProcess.toUpperCase().equalsIgnoreCase("POPULATE-PLAYOFFS") ||
				
				   which_graphics_onscreen == "PLAYOFFS" && whatToProcess.toUpperCase().equalsIgnoreCase("POPULATE-FF-MATCHID")) {
				}else {
					scenes.get(1).setScene_path(valueToProcess.split(",")[1]);
					scenes.get(1).scene_load(session_socket,session_selected_broadcaster);
					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Reset START \0");
//					print_writer.println("-1 RENDERER*STAGE SHOW 0.0\0");
				}
				break;
			default:
				scenes.get(1).setScene_path(valueToProcess.split(",")[1]);
				scenes.get(1).scene_load(session_socket,session_selected_broadcaster);
				print_writer.println("-1 RENDERER*STAGE SHOW 0.0\0");
				break;
			}
			switch (whatToProcess.toUpperCase()) {
			case "POPULATE-SCOREBUG":
				populateScoreBug(false,scorebug,session_socket, valueToProcess.split(",")[1],match, session_selected_broadcaster);
				break;
			case "POPULATE-SCOREBUG-PROMO":
				scorebug.setScorebug_promo(valueToProcess.split(",")[1]);
				populateScoreBugPromo(false,scorebug,session_socket,Integer.valueOf(valueToProcess.split(",")[1]),rugbyService.getTeams(),
						rugbyService.getFixtures(),rugbyService.getGrounds(),match , session_selected_broadcaster);
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Promo_In START \0");
				break;
			case "POPULATE-SCOREBUG_STATS":
				if(scorebug.getLast_scorebug_stat() != null && !scorebug.getLast_scorebug_stat().trim().isEmpty()) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Stats_Out START \0");
					TimeUnit.MILLISECONDS.sleep(500);
					scorebug.setScorebug_stat(valueToProcess.split(",")[1]);
					populateScoreBugStats(false,scorebug,session_socket,Integer.valueOf(valueToProcess.split(",")[2]),Integer.valueOf(valueToProcess.split(",")[3]),
							match,session_selected_broadcaster);
					TimeUnit.MILLISECONDS.sleep(500);
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Stats_In START \0");
				}else {
					scorebug.setScorebug_stat(valueToProcess.split(",")[1]);
					populateScoreBugStats(false,scorebug,session_socket,Integer.valueOf(valueToProcess.split(",")[2]),Integer.valueOf(valueToProcess.split(",")[3]),
							match,session_selected_broadcaster);
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Stats_In START \0");
				}
				break;
			case "POPULATE-SCOREBUG-CARD":	
				if(scorebug.getLast_scorebug_card_goal() != null && !scorebug.getLast_scorebug_card_goal().isEmpty()) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Cards_Out START \0");
					TimeUnit.MILLISECONDS.sleep(500);
					
					scorebug.setScorebug_card_goal(valueToProcess.split(",")[2]);
					populateScorebugCard(scorebug,session_socket, Integer.valueOf(valueToProcess.split(",")[1]),Integer.valueOf(valueToProcess.split(",")[3]), 
							match, session_selected_broadcaster);
					TimeUnit.MILLISECONDS.sleep(500);
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Cards_In START \0");
				}else {
					scorebug.setScorebug_card_goal(valueToProcess.split(",")[2]);
					populateScorebugCard(scorebug,session_socket, Integer.valueOf(valueToProcess.split(",")[1]),Integer.valueOf(valueToProcess.split(",")[3]), 
							match, session_selected_broadcaster);
					TimeUnit.MILLISECONDS.sleep(500);
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Cards_In START \0");
				}
				break;
			case "POPULATE-SCOREBUG-SUBS":
				scorebug.setScorebug_subs(valueToProcess.split(",")[2]);
				populateScorebugSubs(scorebug,session_socket, Integer.valueOf(valueToProcess.split(",")[1]), rugbyService.getAllPlayer(), match, 
						session_selected_broadcaster);
				TimeUnit.MILLISECONDS.sleep(500);
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Substitutes$Subtitutes_In START \0");
				break;
			case "POPULATE-RED_CARD":
				populateRedcard(false,scorebug,session_socket,Integer.valueOf(valueToProcess.split(",")[1]),Integer.valueOf(valueToProcess.split(",")[2]),
						match,session_selected_broadcaster);
				break;
			case "POPULATE-EXTRA_TIME_HALF":
				populateETONE_TWO(false,scorebug,session_socket,match,session_selected_broadcaster);
				break;
			case "POPULATE-EXTRA_TIME":
				populateExtraTime(false,scorebug,session_socket,valueToProcess.split(",")[1],match,session_selected_broadcaster);
				break;
			case "POPULATE-EXTRA_TIME_BOTH":
				populateExtraTimeBoth(false,scorebug,session_socket,valueToProcess.split(",")[1],match,session_selected_broadcaster);
				break;
			case "POPULATE-FF-MATCHID":
				if(which_graphics_onscreen == "") {
					which_side = 1;
					populateMatchId(session_socket,valueToProcess.split(",")[1], match, session_selected_broadcaster,which_side);
				}else {
					which_side = 2;
					populateMatchId(session_socket,valueToProcess.split(",")[1], match, session_selected_broadcaster,which_side);
				}
				break;
			case "POPULATE-PLAYOFFS":
				if(which_graphics_onscreen == "") {
					which_side = 1;
					populatePlayoffs(session_socket,print_writer, valueToProcess.split(",")[1],rugbyService.getPlayoffs(),rugbyService.getTeams(),rugbyService.getVariousTexts(),session_selected_broadcaster,match,which_side);
				}else {
					which_side = 2;
					populatePlayoffs(session_socket,print_writer, valueToProcess.split(",")[1],rugbyService.getPlayoffs(),rugbyService.getTeams(),rugbyService.getVariousTexts(),session_selected_broadcaster,match,which_side);
				}
				break;
			case "POPULATE-FF-TEAMS":
				populateFFTeams(session_socket,valueToProcess.split(",")[1],rugbyService.getTeams(), match, session_selected_broadcaster);
				break;
			case "POPULATE-LT-PROMO":
				populateLTMatchPromoSingle(session_socket, valueToProcess.split(",")[1] ,Integer.valueOf(valueToProcess.split(",")[2]),rugbyService.getTeams(),
						rugbyService.getFixtures(),rugbyService.getGrounds(),match , session_selected_broadcaster);
				break;
			case "POPULATE-FF-PROMO":
				populateMatchPromoSingle(session_socket, valueToProcess.split(",")[1] ,Integer.valueOf(valueToProcess.split(",")[2]),rugbyService.getTeams(),
						rugbyService.getFixtures(),rugbyService.getGrounds(),match , session_selected_broadcaster);
				break;
			case "POPULATE-PENALTY":
				populateLtPenalty(session_socket, valueToProcess.split(",")[1],valueToProcess, rugbyService,match,clock, session_selected_broadcaster);
				break;
			case "POPULATE-CHANGE_PENALTY":
				print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Change_Out START \0");
				TimeUnit.MILLISECONDS.sleep(800);
				populateLtPenaltyChange(session_socket, match,session_selected_broadcaster);
				TimeUnit.MILLISECONDS.sleep(800);
				print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Change_In START \0");
				break;
			case "POPULATE-L3-NAMESUPER":
				//System.out.println("Value1 : " + valueToProcess.split(",")[1] + "Value2 : " + valueToProcess.split(",")[2]);
				for(NameSuper ns : rugbyService.getNameSupers()) {
					  if(ns.getNamesuperId() == Integer.valueOf(valueToProcess.split(",")[2])) {
						  populateNameSuper(session_socket, valueToProcess.split(",")[1], ns, match, session_selected_broadcaster);
					  }
					}
				break;
			case "POPULATE-L3-NAMESUPER-PLAYER":
				populateNameSuperPlayer(session_socket, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]), valueToProcess.split(",")[3], Integer.valueOf(valueToProcess.split(",")[4]), match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-NAMESUPER-CARD":
				populateNameSuperCard(session_socket, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]), valueToProcess.split(",")[3], Integer.valueOf(valueToProcess.split(",")[4]), match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-STAFF":
				for(Staff st : rugbyService.getStaffs()) {
					  if(st.getStaffId() == Integer.valueOf(valueToProcess.split(",")[2])) {
						  populateStaff(session_socket, valueToProcess.split(",")[1], st,rugbyService.getTeams(), match, session_selected_broadcaster);
					  }
					}
				break;
			case "POPULATE-L3-SUBSTITUTE":
				populateSubstitute(session_socket, valueToProcess.split(",")[1],Integer.valueOf(valueToProcess.split(",")[2]),valueToProcess.split(",")[3],
						rugbyService.getAllPlayer(),rugbyService.getTeams(), match, session_selected_broadcaster);
				break;	
			case "POPULATE-FF-PLAYINGXI":
				populatePlayingXI(session_socket, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]),valueToProcess.split(",")[3],rugbyService.getFormations(), rugbyService.getTeams(),
						match, session_selected_broadcaster);
				break;
			case "POPULATE-HOMESUB":
				print_writer.println("-1 RENDERER PREVIEW SCENE*" + "/Default/FullFrames" + " C:/Temp/Preview.png FF_In 0.020 LineUp$Team1$DataIn 2.520 LineUp$Team1$Change 2.100 \0");
				break;
			case "POPULATE-AWAYXI":
				print_writer.println("-1 RENDERER PREVIEW SCENE*" + "/Default/FullFrames" + " C:/Temp/Preview.png FF_In 0.020 LineUp$Team1$DataIn 0.000 LineUp$Team2$DataIn 2.520 \0");
				break;
			case "POPULATE-AWAYSUB":
				print_writer.println("-1 RENDERER PREVIEW SCENE*" + "/Default/FullFrames" + " C:/Temp/Preview.png FF_In 0.020 LineUp$Team2$DataIn 2.520 LineUp$Team2$Change 2.100 \0");
				break;	
			case "POPULATE-L3-HEATMAP":
				populateHeatMapPeakDistance(session_socket, valueToProcess.split(",")[1], Integer.valueOf(valueToProcess.split(",")[2]),valueToProcess.split(",")[3] ,
						Integer.valueOf(valueToProcess.split(",")[4]),rugbyService.getAllPlayer(),match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-TOP_STATS":
				populateTopStats(session_socket, valueToProcess.split(",")[1],valueToProcess.split(",")[2] ,
						RugbyFunctions.getTopStatsDatafromXML(match),rugbyService.getAllPlayer(),rugbyService.getTeams(),match, session_selected_broadcaster);
				break;
			case "POPULATE-L3-SCOREUPDATE":
				populateScoreUpdate(session_socket, valueToProcess.split(",")[1], rugbyService,match,clock, session_selected_broadcaster);
				break;
			case "POPULATE-LT-MATCHID":
				populateLtMatchId(session_socket, valueToProcess.split(",")[1], rugbyService,match,clock, session_selected_broadcaster);
				break;
			case "POPULATE-L3-MATCHSTATUS":
				populateMatchStatus(session_socket, valueToProcess.split(",")[1], match, session_selected_broadcaster);
				break;
			case "POPULATE-OFFICIALS":
				populateOfficials(session_socket, valueToProcess.split(",")[1],rugbyService.getOfficials(),match, session_selected_broadcaster);
				break;
			case "POPULATE-FF-MATCHSTATS":
				populateMatchStats(session_socket,valueToProcess.split(",")[1], rugbyService,match,clock, session_selected_broadcaster);
				break;
			case "POPULATE-DOUBLE_PROMO":
				populateMatchDoublePromo(session_socket, valueToProcess.split(",")[1],valueToProcess.split(",")[2], match,rugbyService.getFixtures(),
						rugbyService.getTeams(),rugbyService.getGrounds(), session_selected_broadcaster);
				break;
			case "POPULATE-LT-RESULT":
				populateMatchResult(session_socket, valueToProcess.split(",")[1],Integer.valueOf(valueToProcess.split(",")[2]),rugbyService.getFixtures(),rugbyService.getTeams(),rugbyService.getGrounds(),
						session_selected_broadcaster,match);
				break;
			case "POPULATE-FIXTURES":
				populateFixtures(session_socket, valueToProcess.split(",")[1],valueToProcess.split(",")[2],valueToProcess.split(",")[3],rugbyService.getFixtures(),rugbyService.getTeams(),rugbyService.getGrounds(),
						session_selected_broadcaster,match);
				break;
			case "POPULATE-QUAIFIERS":
				populateQulifiers(session_socket, valueToProcess.split(",")[1],session_selected_broadcaster,match);
				break;
			case "POPULATE-POINTS_TABLE2":
				LeagueTable league_table1 = null;
				LeagueTable league_table2 = null;
				if(valueToProcess.split(",")[2].equalsIgnoreCase("SemiFinal1")) {
					if(new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableA" + ".XML").exists()) {
						league_table1 = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
								new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableA" + ".XML"));
					}
					if(new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableC" + ".XML").exists()) {
						league_table2 = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
								new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableC" + ".XML"));
					}
					
				}else if(valueToProcess.split(",")[2].equalsIgnoreCase("SemiFinal2")) {
					if(new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableB" + ".XML").exists()) {
						league_table1 = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
								new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableB" + ".XML"));
					}
					if(new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableD" + ".XML").exists()) {
						league_table2 = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
								new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableD" + ".XML"));
					}
				}
				populatePointsTableGrp(session_socket, valueToProcess.split(",")[1],valueToProcess.split(",")[2],league_table1.getLeagueTeams(),league_table2.getLeagueTeams(),
						rugbyService.getTeams(),session_selected_broadcaster,match);
				break;
			case "POPULATE-ROAD-TO-FINAL":
				LeagueTable league_table3 = null;
				LeagueTable league_table4 = null;
				
				if(new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableA" + ".XML").exists()) {
					league_table3 = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
							new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableA" + ".XML"));
				}
				if(new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableB" + ".XML").exists()) {
					league_table4 = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
							new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.LEAGUE_TABLE_DIRECTORY + "LeagueTableB" + ".XML"));
				}
				
				populateRoadToFinal(session_socket, valueToProcess.split(",")[1],league_table3.getLeagueTeams(),league_table4.getLeagueTeams(),
						rugbyService.getTeams(),session_selected_broadcaster,match);
				break;	
			case "POPULATE-POINTS_TABLE":
				LeagueTable league_table = null;
				
				if(new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.LEAGUE_TABLE_DIRECTORY + valueToProcess.split(",")[2] + ".XML").exists()) {
					league_table = (LeagueTable)JAXBContext.newInstance(LeagueTable.class).createUnmarshaller().unmarshal(
							new File(RugbyUtil.RUGBY_DIRECTORY + RugbyUtil.LEAGUE_TABLE_DIRECTORY + valueToProcess.split(",")[2] + ".XML"));
				}
				
				populatePointsTable(session_socket, valueToProcess.split(",")[1],valueToProcess.split(",")[2],league_table.getLeagueTeams(),rugbyService.getTeams(),
						session_selected_broadcaster,match);
				break;
			}
			
		case "NAMESUPER_GRAPHICS-OPTIONS": 
			return JSONArray.fromObject(rugbyService.getNameSupers()).toString();
		case "BUG_DB_GRAPHICS-OPTIONS":
			return JSONArray.fromObject(rugbyService.getBugs()).toString();
		case "STAFF_GRAPHICS-OPTIONS":
			return JSONArray.fromObject(rugbyService.getStaffs()).toString();
		case "PROMO_GRAPHICS-OPTIONS": case "LTPROMO_GRAPHICS-OPTIONS": case "SCOREBUGPROMO_GRAPHICS-OPTIONS": case "RESULT_PROMO_GRAPHICS-OPTIONS":
			return JSONArray.fromObject(RugbyFunctions.processAllFixtures(rugbyService)).toString();
			
		case "ANIMATE-IN-SCOREBUG": case "ANIMATE-IN-SPONSOR": case "ANIMATE-IN-SUBS_CHANGE_ON":
		case "ANIMATE-IN-MATCHID": case "ANIMATE-IN-PROMO": case "ANIMATE-IN-PLAYINGXI": case "ANIMATE-IN-HOMESUB": case "ANIMATE-IN-AWAYXI": case "ANIMATE-IN-AWAYSUB":
		case "ANIMATE-IN-MATCHSTATUS": case "ANIMATE-IN-MATCHSTATS": case "ANIMATE-IN-DOUBLE_PROMO": case "ANIMATE-IN-FF_TEAMS": case "ANIMATE-IN-POINTS_TABLE":
		case "ANIMATE-IN-FIXTURES": case "ANIMATE-IN-QUAIFIERS": case "ANIMATE-IN-POINTS_TABLE2": case "ANIMATE-IN-PLAYOFFS":
		case "ANIMATE-IN-SCOREUPDATE": case "ANIMATE-IN-LT_MATCHID": case "ANIMATE-IN-NAMESUPER_CARD": case "ANIMATE-IN-NAMESUPER": case "ANIMATE-IN-NAMESUPERDB":
		case "ANIMATE-IN-SUBSTITUTE": case "ANIMATE-IN-OFFICIALS": case "ANIMATE-IN-HEATMAP": case "ANIMATE-IN-TOP_STATS": case "ANIMATE-IN-STAFF":
		case "ANIMATE-IN-PENALTY": case "ANIMATE-IN-LTPROMO": case "ANIMATE-IN-RESULT": case "ANIMATE-IN-ROAD-TO-FINAL":
			
		case "ANIMATE-SUB_CHANGE_ON":
		case "CLEAR-ALL": 
		case "ANIMATE-OUT-SCOREBUG": case "ANIMATE-OUT-EXTRA_TIME": case "ANIMATE-OUT-SCOREBUG_STAT": case"ANIMATE-OUT-RED_CARD": case "ANIMATE-OUT-SPONSOR":
		case "ANIMATE-OUT-EXTRA_TIME_HALF":
		case "ANIMATE-OUT": 
			
			switch (whatToProcess.toUpperCase()) {
			case "ANIMATE-IN-SCOREBUG":
				AnimateInGraphics(session_socket, "SCOREBUG");
				is_infobar = true;
				scorebug.setScorebug_on_screen(true);
				break;
			case "ANIMATE-IN-TEST":
				AnimateInGraphics(session_socket, "TEST");
				which_graphics_onscreen = "TEST";
				break;
			case "ANIMATE-IN-QUAIFIERS":
				print_writer.println("-1 RENDERER*STAGE*DIRECTOR*In START \0");
				which_graphics_onscreen = "QUAIFIERS";
				break;
			case "ANIMATE-SUB_CHANGE_ON":
				print_writer.println("-1 RENDERER*STAGE*DIRECTOR*In CONTINUE \0");
				break;
			case "ANIMATE-IN-PLAYOFFS":
				if(which_graphics_onscreen == "") {
					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*In START \0");
					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*FF_In START \0");
//					TimeUnit.MILLISECONDS.sleep(200);
//					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*MatchId_In START \0");
				}else {
					if(which_graphics_onscreen == "MATCHID") {
						print_writer.println("-1 RENDERER*STAGE*DIRECTOR*FF_In START \0");
					}
					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*FF_Change START \0");
					TimeUnit.SECONDS.sleep(7);
					populatePlayoffs(session_socket,print_writer, "/Default/FullFrames_Cut",rugbyService.getPlayoffs(),
							rugbyService.getTeams(),rugbyService.getVariousTexts(),session_selected_broadcaster,match,1);
					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*FF_Change SHOW 0.0\0");
				}
				which_graphics_onscreen = "PLAYOFFS";
				break;
			case "ANIMATE-IN-SPONSOR":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Sponsor_In START \0");
				break;
			case "ANIMATE-IN-SUBS_CHANGE_ON":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Substitutes$Change START \0");
				break;
			case "ANIMATE-IN-FF_TEAMS":
				AnimateInGraphics(session_socket, "FF_TEAMS");
				which_graphics_onscreen = "FF_TEAMS";
				break;
			case "ANIMATE-IN-MATCHID":
				if(which_graphics_onscreen == "") {
					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*In START \0");
//					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*FF_In START \0");
//					TimeUnit.MILLISECONDS.sleep(200);
//					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*MatchId_In START \0");
				}else {
					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*FF_Out START \0");
					TimeUnit.SECONDS.sleep(3);
					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*In START \0");
					TimeUnit.SECONDS.sleep(7);
					populateMatchId(session_socket,"/Default/FullFrames_Cut", match, session_selected_broadcaster,1);
					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*FF_Change SHOW 0.0\0");
				}
//				AnimateInGraphics(session_socket, "MATCHID");
				which_graphics_onscreen = "MATCHID";
				break;
			case "ANIMATE-IN-FIXTURES":
				AnimateInGraphics(session_socket, "FIXTURES");
				which_graphics_onscreen = "FIXTURES";
				break;
			case "ANIMATE-IN-POINTS_TABLE": case "ANIMATE-IN-POINTS_TABLE2": case "ANIMATE-IN-ROAD-TO-FINAL":
				AnimateInGraphics(session_socket, "POINTS_TABLE");
				which_graphics_onscreen = "POINTS_TABLE";
				break;
			case "ANIMATE-IN-MATCHSTATUS":
				AnimateInGraphics(session_socket, "MATCHSTATUS");
				which_graphics_onscreen = "MATCHSTATUS";
				break;
			case "ANIMATE-IN-MATCHSTATS":
				AnimateInGraphics(session_socket, "MATCHSTATS");
				which_graphics_onscreen = "MATCHSTATS";
				break;
			case "ANIMATE-IN-PROMO":
				AnimateInGraphics(session_socket, "MATCHSINGLEPROMO");
				which_graphics_onscreen = "MATCHSINGLEPROMO";
				break;
			case "ANIMATE-IN-DOUBLE_PROMO":
				AnimateInGraphics(session_socket, "DOUBLE_PROMO");
				which_graphics_onscreen = "DOUBLE_PROMO";
				break;
			case "ANIMATE-IN-PENALTY":
				AnimateInGraphics(session_socket, "PENALTY");
				which_graphics_onscreen = "PENALTY";
				break;
			case "ANIMATE-IN-HEATMAP":
				AnimateInGraphics(session_socket, "HEATMAP");
				which_graphics_onscreen = "HEATMAP";
				break;
			case "ANIMATE-IN-TOP_STATS":
				AnimateInGraphics(session_socket, "TOP_STATS");
				which_graphics_onscreen = "TOP_STATS";
				break;
			case "ANIMATE-IN-NAMESUPER_CARD":
				AnimateInGraphics(session_socket, "NAMESUPER_CARD");
				which_graphics_onscreen = "NAMESUPER_CARD";
				break;
			case "ANIMATE-IN-NAMESUPER":
				AnimateInGraphics(session_socket, "NAMESUPER");
				which_graphics_onscreen = "NAMESUPER";
				break;
			case "ANIMATE-IN-STAFF":
				AnimateInGraphics(session_socket, "STAFF");
				which_graphics_onscreen = "STAFF";
				break;
			case "ANIMATE-IN-NAMESUPERDB":
				AnimateInGraphics(session_socket, "NAMESUPERDB");
				which_graphics_onscreen = "NAMESUPERDB";
				break;
			case "ANIMATE-IN-OFFICIALS":
				AnimateInGraphics(session_socket, "OFFICIALS");
				which_graphics_onscreen = "OFFICIALS";
				break;
			case "ANIMATE-IN-RESULT":
				AnimateInGraphics(session_socket, "RESULT");
				which_graphics_onscreen = "RESULT";
				break;
			case "ANIMATE-IN-SUBSTITUTE":
				AnimateInGraphics(session_socket, "SUBSTITUTE");
				which_graphics_onscreen = "SUBSTITUTE";
				break;
			case "ANIMATE-IN-HOMESUB":
				AnimateInGraphics(session_socket, "HOMESUB");
				which_graphics_onscreen = "PLAYINGXI";
				break;
			case "ANIMATE-IN-AWAYXI":
				AnimateInGraphics(session_socket, "AWAYXI");
				which_graphics_onscreen = "PLAYINGXI";
				break;
			case "ANIMATE-IN-AWAYSUB":
				AnimateInGraphics(session_socket, "AWAYSUB");
				which_graphics_onscreen = "PLAYINGXI";
				break;
			case "ANIMATE-IN-PLAYINGXI":
				AnimateInGraphics(session_socket, "PLAYINGXI");
				which_graphics_onscreen = "PLAYINGXI";
				break;
			case "ANIMATE-IN-SCOREUPDATE":
				AnimateInGraphics(session_socket, "SCOREUPDATE");
				TimeUnit.MILLISECONDS.sleep(500);
				if(match.getHomeTeamScore() > 0 || match.getAwayTeamScore() > 0) {
					if(match.getHomeTeamScore() > 4 || match.getAwayTeamScore() > 4) {
						//processAnimation(session_socket, "Scorer3Line_In", "START", session_selected_broadcaster, 2);
					}else if(match.getHomeTeamScore() > 2 || match.getAwayTeamScore() > 2) {
						//processAnimation(session_socket, "Scorer2Line_In", "START", session_selected_broadcaster, 2);
					}else {
						//processAnimation(session_socket, "Scorer1Line_In", "START", session_selected_broadcaster, 2);
					}
				}
				which_graphics_onscreen = "SCOREUPDATE";
				break;
			case "ANIMATE-IN-LTPROMO":
				AnimateInGraphics(session_socket, "LTPROMO");
				which_graphics_onscreen = "LTPROMO";
				break;
			case "ANIMATE-IN-LT_MATCHID":
				AnimateInGraphics(session_socket, "LT_MATCHID");
				which_graphics_onscreen = "LT_MATCHID";
				break;
			case "CLEAR-ALL":
				print_writer.println("-1 SCENE CLEANUP\0");
				print_writer.println("-1 IMAGE CLEANUP\0");
				print_writer.println("-1 GEOM CLEANUP\0");
				print_writer.println("-1 FONT CLEANUP\0");

				print_writer.println("-1 IMAGE INFO\0");
				print_writer.println("-1 RENDERER SET_OBJECT SCENE*" + valueToProcess.split(",")[0] + "\0");

				print_writer.println("-1 RENDERER INITIALIZE\0");
				print_writer.println("-1 RENDERER*SCENE_DATA INITIALIZE\0");
				print_writer.println("-1 RENDERER*UPDATE SET 0\0");
				print_writer.println("-1 RENDERER*STAGE SHOW 0.0\0");

				print_writer.println("-1 RENDERER*UPDATE SET 1\0");

				print_writer.println("-1 RENDERER*FRONT_LAYER SET_OBJECT SCENE*/Default/ScoreBug-Single\0");

				print_writer.println("-1 RENDERER*FRONT_LAYER INITIALIZE\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*SCENE_DATA INITIALIZE\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*UPDATE SET 0\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE SHOW 0.0\0");

				print_writer.println("-1 RENDERER*FRONT_LAYER*UPDATE SET 1\0");

				print_writer.println("-1 SCENE CLEANUP\0");
				print_writer.println("-1 IMAGE CLEANUP\0");
				print_writer.println("-1 GEOM CLEANUP\0");
				print_writer.println("-1 FONT CLEANUP\0");
				which_graphics_onscreen = "";
				is_infobar = false;
				scorebug.setScorebug_on_screen(false);
				break;
			
			case "ANIMATE-OUT-SCOREBUG":
				if(is_infobar == true) {
					AnimateOutGraphics(session_socket, "SCOREBUG");
					is_infobar = false;
					scorebug.setScorebug_on_screen(false);
				}
				break;
			case "ANIMATE-OUT-SPONSOR":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Sponsor_Out START \0");
				break;
			case "ANIMATE-OUT-SCOREBUG_STAT":
				
				if(scorebug.getLast_scorebug_stat() != null && !scorebug.getLast_scorebug_stat().trim().isEmpty()) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Stats_Out START \0");
					scorebug.setLast_scorebug_stat("");scorebug.setScorebug_stat("");
				}else if(scorebug.getLast_scorebug_card_goal() != null && !scorebug.getLast_scorebug_card_goal().isEmpty()) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Cards_Out START \0");
					scorebug.setLast_scorebug_card_goal("");scorebug.setScorebug_card_goal("");
				}else if(scorebug.getLast_scorebug_promo() != null && !scorebug.getLast_scorebug_promo().isEmpty()) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Promo_Out START \0");
					scorebug.setLast_scorebug_promo("");scorebug.setScorebug_promo("");
//					is_this_updating = false;
				}else if(scorebug.getLast_scorebug_subs() != null && !scorebug.getLast_scorebug_subs().isEmpty()) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Substitutes$Subtitutes_Out START \0");
					scorebug.setLast_scorebug_subs("");scorebug.setScorebug_subs("");
				}
				break;
			case"ANIMATE-OUT-RED_CARD":	
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*RedCards_Out START \0");
				break;
			case "ANIMATE-OUT-EXTRA_TIME_HALF":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*ExtraTime_Out START \0");
				break;
			case "ANIMATE-OUT-EXTRA_TIME":
				print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*AddedMin_Out START \0");
				break;
			case "ANIMATE-OUT":
				switch(which_graphics_onscreen) {
				case "MATCHID": case "SCOREUPDATE": case "PLAYINGXI": case "LT_MATCHID": case "NAMESUPER_CARD": case "NAMESUPER": case "NAMESUPERDB": 
				case "SUBSTITUTE": case "MATCHSINGLEPROMO": case "MATCHSTATUS": case "OFFICIALS": case "HEATMAP": case "MATCHSTATS": case "TOP_STATS":
				case "STAFF": case "PENALTY": case "DOUBLE_PROMO": case "FF_TEAMS": case "POINTS_TABLE": case "FIXTURES": case "QUAIFIERS": case "LTPROMO":
				case "PLAYOFFS": case "RESULT": case "ROAD-TO-FINAL":
					AnimateOutGraphics(session_socket, which_graphics_onscreen);
					which_graphics_onscreen = "";
					break;
				}
				break;
			}
			break;
			}
		return null;
	}
	
	public String toString() {
		return "Doad [status=" + status + ", slashOrDash=" + slashOrDash + "]";
	}
	
	public void AnimateInGraphics(Socket session_socket, String whichGraphic) throws InterruptedException, IOException {
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		switch (whichGraphic) {
		case "SCOREBUG":
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*In START \0");
			break;
		case "SCOREUPDATE": case "LT_MATCHID": case "NAMESUPER_CARD": case "NAMESUPER": case "NAMESUPERDB": case "SUBSTITUTE": case "OFFICIALS":
		case "HEATMAP": case "TOP_STATS": case "STAFF": case "PENALTY": case "LTPROMO": case "RESULT":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*In START \0");
			break;
		case "MATCHID": case "MATCHSINGLEPROMO":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*In START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*MatchId_In START \0");
			break;
		case "FIXTURES":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*In START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*FF_In START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Fixtures_In START \0");
			break;
		case "POINTS_TABLE":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*In START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*FF_In START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*PointsTable_In START \0");
			break;
		case "DOUBLE_PROMO":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*In START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*FF_In START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*DoubleID_In START \0");
			break;
		case "FF_TEAMS":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*In START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*FF_In START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Groups_In START \0");
			break;
		case "MATCHSTATUS":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*In START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*FF_In START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*MatchStats_In START \0");
			break;
		case "PLAYOFFS":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*In START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*FF_In START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*PlayOffs_In START \0");
			break;
		case "MATCHSTATS":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*In START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*MatchScorers_In START \0");
			break;
		case "PLAYINGXI":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*In START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*FF_In START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*LineUp$Team1$DataIn START \0");
			break;
		case "HOMESUB":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*LineUp$Team1$Change START \0");
			break;
		case "AWAYXI":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*LineUp$Team1$DataOut START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*LineUp$Team2$DataIn START \0");
			break;
		case "AWAYSUB":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*LineUp$Team2$Change START \0");
			break;	
		}
	}
	public void AnimateOutGraphics(Socket session_socket, String whichGraphic) throws IOException, InterruptedException {
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		switch (whichGraphic.toUpperCase()) {
		case "SCOREBUG":
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*Out START \0");
			break;
		case "SCOREUPDATE": case "LT_MATCHID": case "NAMESUPER_CARD": case "NAMESUPER": case "NAMESUPERDB": case "SUBSTITUTE": case "OFFICIALS":
		case "HEATMAP": case "TOP_STATS": case "STAFF": case "PENALTY": case "QUAIFIERS": case "LTPROMO": case "RESULT":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Out START \0");
			break;
		case "MATCHID": case "MATCHSINGLEPROMO":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*MatchId_Out START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Out START \0");
			break;
		case "PLAYOFFS":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*PlayOffs_Out START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*FF_Out START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Out START \0");
			break;	
		case "FIXTURES":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Fixtures_Out START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*FF_Out START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Out START \0");
			break;
		case "POINTS_TABLE":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*PointsTable_Out START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*FF_Out START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Out START \0");
			break;
		case "DOUBLE_PROMO":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*DoubleID_Out START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*FF_Out START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Out START \0");
			break;
		case "FF_TEAMS":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Groups_Out START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*FF_Out START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Out START \0");
			break;
		case "MATCHSTATUS":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*MatchStats_Out START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*FF_Out START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Out START \0");
			break;
		case "MATCHSTATS":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*MatchScorers_Out START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Out START \0");
			break;
		case "PLAYINGXI":
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*LineUp$Team2$DataOut START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*FF_Out START \0");
			TimeUnit.MILLISECONDS.sleep(200);
			print_writer.println("-1 RENDERER*STAGE*DIRECTOR*Out START \0");
			break;
		}
	}
	
	public ScoreBug populateScoreBug(boolean is_this_updating,ScoreBug scorebug, Socket session_socket,String viz_sence_path,Match match, String selectedbroadcaster) throws IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$MainScorePart$Seperator$AllScoreGrp$txt_HomeScore*GEOM*TEXT SET " + 
					match.getHomeTeamScore() + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$MainScorePart$Seperator$AllScoreGrp$txt_AwayScore*GEOM*TEXT SET " + 
					match.getAwayTeamScore() + "\0");
			
			if(is_this_updating == false) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$MainScorePart$TeamGrp1$txt_Name*GEOM*TEXT SET " + 
						match.getHomeTeam().getTeamName4() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$MainScorePart$TeamGrp2$txt_Name*GEOM*TEXT SET " + 
						match.getAwayTeam().getTeamName4() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$MainScorePart$TeamGrp1$img_TeamColour*TEXTURE*IMAGE SET " + 
						colors_path + match.getHomeTeamJerseyColor() + RugbyUtil.PNG_EXTENSION + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$MainScorePart$TeamGrp2$img_TeamColour*TEXTURE*IMAGE SET " + 
						colors_path + match.getAwayTeamJerseyColor() + RugbyUtil.PNG_EXTENSION + "\0");
				
			}
		}
		return scorebug;
	}
	public ScoreBug populateScoreBugStats(boolean is_this_updating,ScoreBug scorebug, Socket session_socket,int Homedata,int Awaydata ,Match match, String selectedbroadcaster) 
			throws MalformedURLException, IOException, CsvException, InterruptedException {
		
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		
		switch(scorebug.getScorebug_stat().toUpperCase()) {
		case RugbyUtil.YELLOW:
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_StatHead*GEOM*TEXT SET " + "YELLOW CARD" + "\0");
			break;
		case RugbyUtil.RED:
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_StatHead*GEOM*TEXT SET " + "RED CARD" + "\0");
			break;
		case RugbyUtil.OFF_SIDE:
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_StatHead*GEOM*TEXT SET " + "OFFSIDES" + "\0");
			break;
		case RugbyUtil.SHOTS:
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_StatHead*GEOM*TEXT SET " + RugbyUtil.SHOTS + "\0");
			break;
		case RugbyUtil.POSSESSION:
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_StatHead*GEOM*TEXT SET " + RugbyUtil.POSSESSION + " %" + "\0");
			break;
		case RugbyUtil.SHOTS_ON_TARGET:
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_StatHead*GEOM*TEXT SET " + "SHOTS ON TARGET" + "\0");
			break;
		case RugbyUtil.CORNERS:
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_StatHead*GEOM*TEXT SET " + RugbyUtil.CORNERS + "\0");
			break;
		case RugbyUtil.TACKLES:
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_StatHead*GEOM*TEXT SET " + RugbyUtil.TACKLES + "\0");
			break;
		}
		
		print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_HomeStatValue*GEOM*TEXT SET " + 
				Homedata + "\0");
		print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$StatsGrp$StatDataGrp$txt_AwayStatValue*GEOM*TEXT SET " + 
				Awaydata + "\0");
		
		scorebug.setLast_scorebug_stat(scorebug.getScorebug_stat().toUpperCase());
		return scorebug;
	}
	public ScoreBug populateRedcard(boolean is_this_updating, ScoreBug scorebug, Socket session_socket,int Homedata,int Awaydata, Match match, String selectedbroadcaster) throws MalformedURLException, IOException, CsvException {
		
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$MainScorePart$TeamGrp1$CardGrp$SelectCardNumber*FUNCTION*Omo*vis_con SET " + Homedata + "\0");
		
		print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$MainScorePart$TeamGrp2$CardGrp$SelectCardNumber*FUNCTION*Omo*vis_con SET " + Awaydata + "\0");
		
		print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*RedCards_In START \0");
		
		return scorebug;
	}
	public ScoreBug populateETONE_TWO(boolean is_this_updating, ScoreBug scorebug, Socket session_socket, Match match, String selectedbroadcaster) throws MalformedURLException, IOException, CsvException {
		
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		
		if(match.getClock().getMatchHalves().equalsIgnoreCase(RugbyUtil.EXTRA1) || match.getClock().getMatchHalves().equalsIgnoreCase(RugbyUtil.EXTRA2)) {
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$ExtraPart$TimeIn$txt_ExtraTime*GEOM*TEXT SET " + "ET" + "\0");
			
		}else {
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$ExtraPart$TimeIn$txt_ExtraTime*GEOM*TEXT SET " + "" + "\0");
		}
		
		print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*ExtraTime_In START \0");
		
		return scorebug;
	}
	public ScoreBug populateExtraTime(boolean is_this_updating,ScoreBug scorebug, Socket session_socket,String time_value, Match match, String selectedbroadcaster) throws IOException {
		
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		if(is_this_updating == false) {
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$TimePart$TimeIn$TimePosition$InjuryTimeGrp$txt_AddedMinute*GEOM*TEXT SET " + "+" +  time_value + "'" + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*AddedMin_In START \0");
		}
		return scorebug;
	}
	public ScoreBug populateExtraTimeBoth(boolean is_this_updating,ScoreBug scorebug,Socket session_socket,String time_value, Match match, String selectedbroadcaster) throws IOException {
		
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		if(is_this_updating == false) {
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$TimePart$TimeIn$TimePosition$InjuryTimeGrp$txt_AddedMinute*GEOM*TEXT SET " + "+" +  time_value + "'" + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*STAGE*DIRECTOR*AddedMin_In START \0");
		}
		return scorebug;
	}
	public ScoreBug populateScorebugCard(ScoreBug scorebug,Socket session_socket,int TeamId,int playerId, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			int l = 200;
			String team_name = "";
			if(TeamId == match.getHomeTeamId()) {
				team_name = match.getHomeTeam().getTeamName1();
				for(Player hs : match.getHomeSquad()) {
					if(playerId == hs.getPlayerId()) {
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$txt_Number*GEOM*TEXT SET " + hs.getJersey_number() + "\0");
						
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$PlayerName$txt_FirstName*GEOM*TEXT SET " + "" + "\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$PlayerName$txt_LastName*GEOM*TEXT SET " + hs.getTicker_name().toUpperCase() + "\0");
						
					}
				}
				for(Player hsub : match.getHomeSubstitutes()) {
					if(playerId == hsub.getPlayerId()) {
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$txt_Number*GEOM*TEXT SET " + hsub.getJersey_number() + "\0");
						
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$PlayerName$txt_FirstName*GEOM*TEXT SET " + "" + "\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$PlayerName$txt_LastName*GEOM*TEXT SET " + hsub.getTicker_name().toUpperCase() + "\0");
					}
				}
			}
			else {
				team_name = match.getAwayTeam().getTeamName1();
				for(Player as : match.getAwaySquad()) {
					if(playerId == as.getPlayerId()) {
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$txt_Number*GEOM*TEXT SET " + as.getJersey_number() + "\0");
						
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$PlayerName$txt_FirstName*GEOM*TEXT SET " + "" + "\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$PlayerName$txt_LastName*GEOM*TEXT SET " + as.getTicker_name().toUpperCase() + "\0");
						
					}
				}
				for(Player asub : match.getAwaySubstitutes()) {
					if(playerId == asub.getPlayerId()) {
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$txt_Number*GEOM*TEXT SET " + asub.getJersey_number() + "\0");
						
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$PlayerName$txt_FirstName*GEOM*TEXT SET " + "" + "\0");
						print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$PlayerName$txt_LastName*GEOM*TEXT SET " + asub.getTicker_name().toUpperCase() + "\0");
					}
				}
			}
			
			switch(scorebug.getScorebug_card_goal().toUpperCase())
			{
			case "YELLOW_CARD":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$SelectCardType*ACTIVE SET 1 \0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$SelectCardType*FUNCTION*Omo*vis_con SET " + "0" + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$txt_TeamName*GEOM*TEXT SET " + team_name + "\0");
				break;
			case "RED_CARD":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$SelectCardType*ACTIVE SET 1 \0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$SelectCardType*FUNCTION*Omo*vis_con SET " + "1" + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$txt_TeamName*GEOM*TEXT SET " + team_name + "\0");
				break;
			case "YELLOW_RED":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$SelectCardType*ACTIVE SET 1 \0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$SelectCardType*FUNCTION*Omo*vis_con SET " + "2" + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$txt_TeamName*GEOM*TEXT SET " + team_name + "\0");
				break;
			case "PLAYER":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$PlayerGrpAll$SelectCardType*ACTIVE SET 0 \0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$CardsAll$txt_TeamName*GEOM*TEXT SET " + team_name + "\0");
				break;
			}
			scorebug.setLast_scorebug_card_goal(scorebug.getScorebug_card_goal().toUpperCase());
		}
		return scorebug;
	}
	public ScoreBug populateScorebugSubs(ScoreBug scorebug,Socket session_socket,int TeamId,List<Player> plyr, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			int l = 200;
			List<Event> evnt = new ArrayList<Event>();
			
			for(int i = 0; i<=match.getEvents().size()-1; i++) { 
				if(match.getEvents().get(i).getEventType().equalsIgnoreCase("replace")) {
					if(TeamId ==plyr.get(match.getEvents().get(i).getOnPlayerId()-1).getTeamId()) {
						if(match.getHomeTeamId() == plyr.get(match.getEvents().get(i).getOnPlayerId()-1).getTeamId()) {
							evnt.add(match.getEvents().get(i)); 
						}else if(match.getAwayTeamId() == plyr.get(match.getEvents().get(i).getOnPlayerId()-1).getTeamId()) {
							evnt.add(match.getEvents().get(i)); 
						} 
					} 
				}
			}
			if(match.getHomeTeamId() == TeamId) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$TeamNameGrp$txt_TeamName*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1() + "\0");
			}else if(match.getAwayTeamId() == TeamId) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$TeamNameGrp$txt_TeamName*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1() + "\0");
			}
			switch(scorebug.getScorebug_subs().toUpperCase())
			{
			case "SINGLE":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll*FUNCTION*Omo*vis_con SET " + "1" + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$OutPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$OutPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$OutPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getTicker_name().toUpperCase() + "\0");
				
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$InPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$InPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$InPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTicker_name().toUpperCase() + "\0");
				
				break;
			case "DOUBLE":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll*FUNCTION*Omo*vis_con SET " + "2" + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$OutPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$OutPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$OutPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getTicker_name().toUpperCase() + "\0");
				
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$InPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$InPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$InPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getTicker_name().toUpperCase() + "\0");
				
				
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$OutPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$OutPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$OutPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getTicker_name().toUpperCase() + "\0");
				
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$InPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$InPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$InPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTicker_name().toUpperCase() + "\0");
				
				break;
				
			case "TRIPLE":
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll*FUNCTION*Omo*vis_con SET " + "3" + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$OutPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 3).getOffPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$OutPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$OutPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 3).getOffPlayerId() - 1).getTicker_name().toUpperCase() + "\0");
				
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$InPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 3).getOnPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$InPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line1$InPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 3).getOnPlayerId() - 1).getTicker_name().toUpperCase() + "\0");
				
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$OutPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$OutPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$OutPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getTicker_name().toUpperCase() + "\0");
				
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$InPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$InPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line2$InPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getTicker_name().toUpperCase() + "\0");
				
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line3$OutPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line3$OutPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line3$OutPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getTicker_name().toUpperCase() + "\0");
				
				
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line3$InPlayerGrpAll$Player$txt_Number*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line3$InPlayerGrpAll$Player$PlayerNameAll$txt_FirstName*GEOM*TEXT SET " + 
						"" + "\0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$DataGrp$SubstitutesAll$Grp1$SubDataAll$PlayerAll$Line3$InPlayerGrpAll$Player$PlayerNameAll$txt_LastName*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTicker_name().toUpperCase() + "\0");
				
				break;
			}
			
		}
		
		scorebug.setLast_scorebug_subs(scorebug.getScorebug_subs().toUpperCase());
		return scorebug;
	}
	
	public void populateFFTeams(Socket session_socket,String viz_scene, List<Team> team,Match match, String session_selected_broadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			int row_id_1=0,row_id_2=0,row_id_3=0,row_id_4=0;
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$GroupsAll$AllData$BottomInfoGrp$txt_Info*GEOM*TEXT SET " + 
					"GROUP WINNER WILL QUALIFY FOR THE SEMI-FINALS" + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0 \0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$GroupsAll$AllData$txt_Header*GEOM*TEXT SET " + "GROUPS" + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$GroupsAll$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET " + match.getTournament() + "\0");
			
			for(int i=0;i<=team.size()-1;i++) {
				if(team.get(i).getTeamGroup().equalsIgnoreCase("A")) {
					row_id_1 = row_id_1 + 1;
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$GroupsAll$AllData$Group1$TeamData" + row_id_1 + 
							"$TeamAll$Team$txt_TeamName*GEOM*TEXT SET " + team.get(i).getTeamName1() + "\0");
				}else if(team.get(i).getTeamGroup().equalsIgnoreCase("B")) {
					row_id_2 = row_id_2 + 1;
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$GroupsAll$AllData$Group2$TeamData" + row_id_2 + 
							"$TeamAll$Team$txt_TeamName*GEOM*TEXT SET " + team.get(i).getTeamName1() + "\0");
				}else if(team.get(i).getTeamGroup().equalsIgnoreCase("C")) {
					row_id_3 = row_id_3 + 1;
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$GroupsAll$AllData$Group3$TeamData" + row_id_3 + 
							"$TeamAll$Team$txt_TeamName*GEOM*TEXT SET " + team.get(i).getTeamName1() + "\0");
				}else if(team.get(i).getTeamGroup().equalsIgnoreCase("D")) {
					row_id_4 = row_id_4 + 1;
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$GroupsAll$AllData$Group4$TeamData" + row_id_4 + 
							"$TeamAll$Team$txt_TeamName*GEOM*TEXT SET " + team.get(i).getTeamName1() + "\0");
				}
			}
			
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 FF_In 2.000 Groups_In 2.580 \0");
		}
	}
	public void populateMatchId(Socket session_socket,String viz_scene, Match match, String session_selected_broadcaster,int whichside) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			if(whichside == 1) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "*FUNCTION*Omo*vis_con SET 0\0");
			}else {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "*FUNCTION*Omo*vis_con SET 0\0");
			}
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$MatchId$All$TeamGrp1$ImageGrp$LogoImageGrp1$img_BadgesBW"
					+ "*TEXTURE*IMAGE SET "+ logo_bw_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$MatchId$All$TeamGrp1$ImageGrp$LogoImageGrp2$img_BadgesOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$MatchId$All$TeamGrp1$ImageGrp$LogoImageGrp3$img_BadgesOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$MatchId$All$TeamGrp1$ImageGrp$LogoImageGrp4$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$MatchId$All$TeamGrp2$ImageGrp$LogoImageGrp1$img_BadgesBW"
					+ "*TEXTURE*IMAGE SET "+ logo_bw_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$MatchId$All$TeamGrp2$ImageGrp$LogoImageGrp2$img_BadgesOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$MatchId$All$TeamGrp2$ImageGrp$LogoImageGrp3$img_BadgesOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$MatchId$All$TeamGrp2$ImageGrp$LogoImageGrp4$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$MatchId$All$TeamGrp1$NameGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
					colors_path + match.getHomeTeamJerseyColor() + RugbyUtil.PNG_EXTENSION + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$MatchId$All$TeamGrp2$NameGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
					colors_path + match.getAwayTeamJerseyColor() + RugbyUtil.PNG_EXTENSION + "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$MatchId$All$SelectSeparator$txt_Score*GEOM*TEXT SET " + "VS" + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$MatchId$All$HashTag$txt_WebInfo*GEOM*TEXT SET " + "" + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$MatchId$All$Header$txt_Header*GEOM*TEXT SET " + match.getTournament() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$MatchId$All$TeamGrp1$NameGrp$txt_TeamName*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$MatchId$All$TeamGrp2$NameGrp$txt_TeamName*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$MatchId$All$NameGrp$txt_Info*GEOM*TEXT SET " + match.getVenueName().toUpperCase() + "\0");
			
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 MatchId_In 2.600 \0");
		}
	}
	public void populateMatchPromoSingle(Socket session_socket,String viz_scene, int match_number ,List<Team> team,List<Fixture> fix,List<Ground>ground,Match match, String broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {			
			String newDate = "";
			
			String[] dateSuffix = {
					"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
					
					"th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
					
					"th", "st", "nd", "rd", "th", "th", "th", "th", "th","th",
					
					"th", "st"
			};
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$Header$txt_Header*GEOM*TEXT SET " + fix.get(match_number-1).getMatchfilename() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$All$SelectSeparator$txt_Score*GEOM*TEXT SET " + "VS" + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$All$HashTag$txt_WebInfo*GEOM*TEXT SET " + "" + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$All$TeamGrp1$NameGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
					colors_path + "WHITE" + RugbyUtil.PNG_EXTENSION + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$All$TeamGrp2$NameGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
					colors_path + "WHITE" + RugbyUtil.PNG_EXTENSION + "\0");
			
			for(Team TM : team) {
				if(fix.get(match_number - 1).getHometeamid() == TM.getTeamId()) {

					print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$All$TeamGrp1$ImageGrp$LogoImageGrp1$img_BadgesBW"
							+ "*TEXTURE*IMAGE SET "+ logo_bw_path + TM.getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$All$TeamGrp1$ImageGrp$LogoImageGrp2$img_BadgesOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + TM.getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$All$TeamGrp1$ImageGrp$LogoImageGrp3$img_BadgesOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + TM.getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$All$TeamGrp1$ImageGrp$LogoImageGrp4$img_Badges"
							+ "*TEXTURE*IMAGE SET "+ logo_path + TM.getTeamName4().toLowerCase() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$All$TeamGrp1$NameGrp$txt_TeamName*GEOM*TEXT SET " + TM.getTeamName1().toUpperCase() + "\0");
				}
				if(fix.get(match_number - 1).getAwayteamid() == TM.getTeamId()) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$TeamBadgeGrp2$BadgeAll$img_Badge" + "*TEXTURE*IMAGE SET "+ logo_path + 
							TM.getTeamName2().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$All$TeamGrp2$ImageGrp$LogoImageGrp1$img_BadgesBW"
							+ "*TEXTURE*IMAGE SET "+ logo_bw_path + TM.getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$All$TeamGrp2$ImageGrp$LogoImageGrp2$img_BadgesOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + TM.getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$All$TeamGrp2$ImageGrp$LogoImageGrp3$img_BadgesOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + TM.getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$All$TeamGrp2$ImageGrp$LogoImageGrp4$img_Badges"
							+ "*TEXTURE*IMAGE SET "+ logo_path +TM.getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$All$TeamGrp2$NameGrp$txt_TeamName*GEOM*TEXT SET " + TM.getTeamName1().toUpperCase() + "\0");	
				}
			}
			String Date = "";
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, +1);
			Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
			if(fix.get(match_number-1).getDate().equalsIgnoreCase(Date)) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$All$NameGrp$txt_Info*GEOM*TEXT SET " + "TOMORROW AT " + fix.get(match_number-1).getTime() 
						+ " LOCAL TIME (" + ground.get(fix.get(match_number -1).getVenue() - 1).getFullname() + ")" + "\0");
			}else {
				cal.add(Calendar.DATE, -1);
				Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
				if(fix.get(match_number-1).getDate().equalsIgnoreCase(Date)) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$All$NameGrp$txt_Info*GEOM*TEXT SET " + "COMING UP AT " + fix.get(match_number-1).getTime() 
							+ " LOCAL TIME (" + ground.get(fix.get(match_number -1).getVenue() - 1).getFullname() + ")" + "\0");
				}else {
					newDate = fix.get(match_number-1).getDate().split("-")[0];
					if(Integer.valueOf(newDate) < 10) {
						newDate = newDate.replaceFirst("0", "");
					}
					print_writer.println("-1 RENDERER*TREE*$Main$All$MatchId$All$NameGrp$txt_Info*GEOM*TEXT SET " + newDate + dateSuffix[Integer.valueOf(newDate)] + " MAY" + " AT " + fix.get(match_number-1).getTime() 
							+ " LOCAL TIME (" + ground.get(fix.get(match_number -1).getVenue() - 1).getFullname() + ")" + "\0");
				}
				
			}
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 MatchId_In 2.100 \0");	
		}
	}
	public void populateMatchStatus(Socket session_socket,String viz_scene,Match match, String session_selected_broadcaster) throws InterruptedException, IOException, CsvException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0 \0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp1$LogoImageGrp1$img_BadgesBW"
					+ "*TEXTURE*IMAGE SET "+ logo_bw_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp1$LogoImageGrp2$img_BadgesOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp1$LogoImageGrp3$img_BadgesOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp1$LogoImageGrp4$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp2$LogoImageGrp1$img_BadgesBW"
					+ "*TEXTURE*IMAGE SET "+ logo_bw_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp2$LogoImageGrp2$img_BadgesOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp2$LogoImageGrp3$img_BadgesOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$TeamLogoGrp2$LogoImageGrp4$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$SponsorPosition$SponsorGrpAll$SelectSponsorType$SponsorAll$SponsorBase"
					+ "*TEXTURE*IMAGE SET "+ logo_path + "TLogo" + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_Header*GEOM*TEXT SET " + 
					match.getTournament() + "\0");
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase("HALF")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_SubHead*GEOM*TEXT SET " + 
						match.getClock().getMatchHalves().toUpperCase() + " TIME" + "\0");
				
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FULL")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_SubHead*GEOM*TEXT SET " + 
						match.getClock().getMatchHalves().toUpperCase() + " TIME" + "\0");
				
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FIRST")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_SubHead*GEOM*TEXT SET " + "FIRST HALF" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("SECOND")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_SubHead*GEOM*TEXT SET " + "SECOND HALF" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA1") || match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA2")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$HeaderDataAll$txt_SubHead*GEOM*TEXT SET " + "EXTRA TIME" + "\0");
			}
			
			print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$TeamDataGrp$Out$TeamData$TextGrp$ScoreAllGrp$txt_HomeTeamScore*GEOM*TEXT SET " + 
					match.getHomeTeamScore() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$TeamDataGrp$Out$TeamData$TextGrp$ScoreAllGrp$txt_AwayTeamScore*GEOM*TEXT SET " + 
					match.getAwayTeamScore() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$TeamDataGrp$Out$TeamData$TextGrp$txt_HomeTeamName*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$TeamDataGrp$Out$TeamData$TextGrp$txt_AwayTeamName*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
			
			
			print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row1$Out$StatAllGrp$StatDataAll$txt_StatHead*GEOM*TEXT SET " + "POSSESSION (%)" + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row2$Out$StatAllGrp$StatDataAll$txt_StatHead*GEOM*TEXT SET " + RugbyUtil.SHOTS + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row3$Out$StatAllGrp$StatDataAll$txt_StatHead*GEOM*TEXT SET " + "SHOTS ON TARGET" + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row4$Out$StatAllGrp$StatDataAll$txt_StatHead*GEOM*TEXT SET " + RugbyUtil.YELLOW + " CARDS" + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row5$Out$StatAllGrp$StatDataAll$txt_StatHead*GEOM*TEXT SET " + RugbyUtil.RED + " CARDS" + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row6$Out$StatAllGrp$StatDataAll$txt_StatHead*GEOM*TEXT SET " + RugbyUtil.CORNERS + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row7$Out$StatAllGrp$StatDataAll$txt_StatHead*GEOM*TEXT SET " + "OFFSIDES" + "\0");
			
			
			String text_to_return = "";
			ArrayList<String> Stats = new ArrayList<String>();
			try (BufferedReader br = new BufferedReader(new FileReader(RugbyUtil.RUGBY_DIRECTORY + "Stats.txt"))) {
				while((text_to_return = br.readLine()) != null) {
				    Stats.add(text_to_return);
				}
			}
		
		    for(int i=0;i<=Stats.size()-1;i++) {
		    	//System.out.println("VALUE : " + Stats.get(i));
		    	print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row" + (i+1) + "$Out$StatAllGrp$StatDataAll"
		    			+ "$txt_HomeStatValue*GEOM*TEXT SET " + Stats.get(i).split(",")[0] + "\0");
		    	print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$MatchStatsAll$AllData$DataOut$StatDataAll$Row" + (i+1) + "$Out$StatAllGrp$StatDataAll"
		    			+ "$txt_AwayStatValue*GEOM*TEXT SET " + Stats.get(i).split(",")[2] + "\0");
		    }
			
		    print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 FF_In 2.000 MatchStats_In 2.200 \0");
		}
	}
	public void populateMatchStats(Socket session_socket,String viz_scene,RugbyService rugbyService, Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			//int l = 4;
			//String Home_player="",Away_player="";
			
			//print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$Header$HeaderOut$txt_TopHeader*GEOM*TEXT SET " + match.getMatchIdent().toUpperCase() + " - " + match.getTournament() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$Header$HeaderOut$txt_Score*GEOM*TEXT SET " + match.getHomeTeamScore() + " - " + match.getAwayTeamScore() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$TeamBadgeGrp1$BadgeAll$img_Badge" + "*TEXTURE*IMAGE SET "+ logo_path + 
					match.getHomeTeam().getTeamName2().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$TeamBadgeGrp2$BadgeAll$img_Badge" + "*TEXTURE*IMAGE SET "+ logo_path + 
					match.getAwayTeam().getTeamName2().toLowerCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$TeamGrp1$txt_TeamName*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$TeamGrp2$txt_TeamName*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase("HALF")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$Header$SubHeaderAll$SubHeadOut$SubHeadIn$txt_GamePart*GEOM*TEXT SET " + 
						match.getClock().getMatchHalves().toUpperCase() + " TIME" + "\0");
				
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FULL")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$Header$SubHeaderAll$SubHeadOut$SubHeadIn$txt_GamePart*GEOM*TEXT SET " + 
						match.getClock().getMatchHalves().toUpperCase() + " TIME" + "\0");
				
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("FIRST")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$Header$SubHeaderAll$SubHeadOut$SubHeadIn$txt_GamePart*GEOM*TEXT SET " + "FIRST HALF" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("SECOND")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$Header$SubHeaderAll$SubHeadOut$SubHeadIn$txt_GamePart*GEOM*TEXT SET " + "SECOND HALF" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA1")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$Header$SubHeaderAll$SubHeadOut$SubHeadIn$txt_GamePart*GEOM*TEXT SET " + "EXTRA TIME 1" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("EXTRA2")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$Header$SubHeaderAll$SubHeadOut$SubHeadIn$txt_GamePart*GEOM*TEXT SET " + "EXTRA TIME 2" + "\0");
			}
			
			List<String> home_stats = new ArrayList<String>();
			List<String> away_stats = new ArrayList<String>();
			List<Integer> plyr_ids = new ArrayList<Integer>();
			boolean plyr_exist = false;
 			String stats_txt = "",stats_txt_og = "";
			
			for(int i=0; i<=match.getMatchStats().size()-1; i++) {
				
				if((match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(RugbyUtil.GOAL) 
						|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(RugbyUtil.PENALTY))) {
					
					plyr_exist = false;
					for(Integer plyr_id : plyr_ids) {
						if(match.getMatchStats().get(i).getPlayerId() == plyr_id && 
								(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(RugbyUtil.GOAL) 
										|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(RugbyUtil.OWN_GOAL)
										|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(RugbyUtil.PENALTY))) {
							plyr_exist = true;
							break;
						}
					}

					if(plyr_exist == false) {
						plyr_ids.add(match.getMatchStats().get(i).getPlayerId());
						stats_txt = rugbyService.getPlayer(RugbyUtil.PLAYER, 
							String.valueOf(match.getMatchStats().get(i).getPlayerId())).getTicker_name().toUpperCase()+ " " + 
							RugbyFunctions.calExtraTimeGoal(match.getMatchStats().get(i).getMatchHalves(),match.getMatchStats().get(i).getTotalMatchSeconds()) + 
								RugbyFunctions.goal_shortname(match.getMatchStats().get(i).getStats_type());
						
						for(int j=i+1; j<=match.getMatchStats().size()-1; j++) {
							if (match.getMatchStats().get(i).getPlayerId() == match.getMatchStats().get(j).getPlayerId()
								&& (match.getMatchStats().get(j).getStats_type().equalsIgnoreCase(RugbyUtil.GOAL)
								|| match.getMatchStats().get(j).getStats_type().equalsIgnoreCase(RugbyUtil.PENALTY))) {

								stats_txt = stats_txt + "," + 
									RugbyFunctions.calExtraTimeGoal(match.getMatchStats().get(j).getMatchHalves(), match.getMatchStats().get(j).getTotalMatchSeconds()) 
										+ RugbyFunctions.goal_shortname(match.getMatchStats().get(j).getStats_type());
							}
						}
						switch (RugbyFunctions.getPlayerSquadType(match.getMatchStats().get(i).getPlayerId(),match.getMatchStats().get(i).getStats_type() ,match)) {
						case RugbyUtil.HOME:
							home_stats.add(stats_txt);
							break;
						case RugbyUtil.AWAY:
							away_stats.add(stats_txt);
							break;
						}
					}
				}else if(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(RugbyUtil.OWN_GOAL)) {
					stats_txt_og = rugbyService.getPlayer(RugbyUtil.PLAYER, 
							String.valueOf(match.getMatchStats().get(i).getPlayerId())).getTicker_name().toUpperCase()+ " " + 
							RugbyFunctions.calExtraTimeGoal(match.getMatchStats().get(i).getMatchHalves(),match.getMatchStats().get(i).getTotalMatchSeconds()) + 
								RugbyFunctions.goal_shortname(match.getMatchStats().get(i).getStats_type());
						
						/*for(int j=i+1; j<=match.getMatchStats().size()-1; j++) {
							if (match.getMatchStats().get(i).getPlayerId() == match.getMatchStats().get(j).getPlayerId()
								&& (match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(RugbyUtil.OWN_GOAL))) {

								stats_txt_og = stats_txt_og + "," + 
									RugbyFunctions.calExtraTimeGoal(match.getMatchStats().get(j).getMatchHalves(), match.getMatchStats().get(j).getTotalMatchSeconds()) 
										+ RugbyFunctions.goal_shortname(match.getMatchStats().get(j).getStats_type());
							}
						}*/
						switch (RugbyFunctions.getPlayerSquadType(match.getMatchStats().get(i).getPlayerId(),match.getMatchStats().get(i).getStats_type() ,match)) {
						case RugbyUtil.HOME:
							home_stats.add(stats_txt_og);
							break;
						case RugbyUtil.AWAY:
							away_stats.add(stats_txt_og);
							break;
						}
				}
			}
			
			if(match.getHomeTeamScore() == 0 && match.getAwayTeamScore() == 0) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam1*ACTIVE SET 0 \0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam2*ACTIVE SET 0 \0");
			}else if(match.getHomeTeamScore() == 1 && match.getAwayTeamScore() == 0) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam1*ACTIVE SET 1 \0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam2*ACTIVE SET 0 \0");
				
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam1*FUNCTION*Grid*num_row SET " + home_stats.size() + "\0");
			}else if(match.getHomeTeamScore() == 0 && match.getAwayTeamScore() == 1) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam1*ACTIVE SET 0 \0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam2*ACTIVE SET 1 \0");
				
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam2*FUNCTION*Grid*num_row SET " + away_stats.size() + "\0");
			}else {
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam1*ACTIVE SET 1 \0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam2*ACTIVE SET 1 \0");
				
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam1*FUNCTION*Grid*num_row SET " + home_stats.size() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam2*FUNCTION*Grid*num_row SET " + away_stats.size() + "\0");
			}
			
			for(int i=0;i<=home_stats.size()-1;i++) {
				if(i<=6) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam1$Scorer" + (i+1) + 
							"$Out$In$txt_Scorer*GEOM*TEXT SET " + home_stats.get(i) + "\0");
				}
			}
			
			for(int i=0;i<=away_stats.size()-1;i++) {
				if(i<=6) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$MatchScorers$DataAll$DataOut$ScorrerTeam2$Scorer" + (i+1) + 
							"$Out$In$txt_Scorer*GEOM*TEXT SET " + away_stats.get(i) + "\0");
				}
			}
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 MatchId_In 1.700 \0");
		}
	}
	public void populatePlayingXI(Socket session_socket,String viz_scene, int TeamId,String Type,List<Formation> formation, List<Team> team ,Match match, String session_selected_broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			int row_id = 0,row_id_sub = 0,l=100;
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0 \0");
			
			if(TeamId == match.getHomeTeamId()) {
				
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$txt_TeamName*GEOM*TEXT SET " + 
						team.get(TeamId-1).getTeamName1().toUpperCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$SubHeadGrp$txt_SubHead1*GEOM*TEXT SET " + 
						"STARTING XI" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$SubHeadGrp$txt_SubHead2*GEOM*TEXT SET " + 
						"SUBSTITUTES" + "\0");
				
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$LogoGrp$LogoImageGrp1$img_BadgesBW"
						+ "*TEXTURE*IMAGE SET "+ logo_bw_path + team.get(TeamId-1).getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$LogoGrp$LogoImageGrp4$img_Badges"
						+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(TeamId-1).getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$LogoGrp$LogoImageGrp2$img_BadgesOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + team.get(TeamId-1).getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$LogoGrp$LogoImageGrp3$img_BadgesOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + team.get(TeamId-1).getTeamName4().toLowerCase() + "\0");
				
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
						colors_path + match.getHomeTeamJerseyColor() + RugbyUtil.PNG_EXTENSION + "\0");
				
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player1"
						+ "$In$SelectPlayerType$GK_Jersey$img_GK_Jersey*TEXTURE*IMAGE SET "+ colors_path + match.getHomeTeamGKJerseyColor().toUpperCase() + RugbyUtil.PNG_EXTENSION + "\0");
				
				if(team.get(TeamId-1).getTeamCoach() == null) {
					if(team.get(TeamId-1).getTeamAssistantCoach() == null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$txt_CoachText*GEOM*TEXT SET " + 
								"" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_FirstName*GEOM*TEXT SET " + 
								"" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_LastName*GEOM*TEXT SET " + 
								"" + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$txt_CoachText*GEOM*TEXT SET " + 
								"ASST. COACH" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_FirstName*GEOM*TEXT SET " + 
								"" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_LastName*GEOM*TEXT SET " + 
								team.get(TeamId-1).getTeamAssistantCoach() + "\0");
					}
					
				}else {
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$txt_CoachText*GEOM*TEXT SET " + 
							"COACH" + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_FirstName*GEOM*TEXT SET " + 
							"" + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_LastName*GEOM*TEXT SET " + 
							team.get(TeamId-1).getTeamCoach() + "\0");
				}
				
				
				
				for(Player hs : match.getHomeSquad()) {
					row_id = row_id + 1;
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp"
							+ "*FUNCTION*Grid*num_row SET " + row_id + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
							"$Out$Dehighlight$NumberGrp$txt_Number*GEOM*TEXT SET " + hs.getJersey_number() + "\0");
					
					if(hs.getSurname() != null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$NameAll$txt_FirstName*GEOM*TEXT SET " + hs.getFirstname().toUpperCase() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$NameAll$txt_LastName*GEOM*TEXT SET " + hs.getSurname().toUpperCase() + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$NameAll$txt_FirstName*GEOM*TEXT SET " + hs.getFull_name().toUpperCase() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$NameAll$txt_LastName*GEOM*TEXT SET " + "" + "\0");
					}
					
					
					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(RugbyUtil.CAPTAIN)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectCaptain*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(RugbyUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectCaptain*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectCaptain*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectCaptain*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
					//FORMATION
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player" + row_id + 
							"$Out$In$Dehighlight$Radikal-Bold*GEOM*TEXT SET " + hs.getJersey_number() + "\0");
					
					switch(Type.toUpperCase()) {
					case "WITHOUT_IMAGE":
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
								"$In$SelectPlayerType*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player"+ row_id + 
							"$In$SelectPlayerType$Jersey$img_Jersey"+ "*TEXTURE*IMAGE SET "+ colors_path + match.getHomeTeamJerseyColor().toUpperCase() + RugbyUtil.PNG_EXTENSION + "\0");
						
						if(match.getHomeTeamJerseyColor().equalsIgnoreCase("WHITE")) {
							print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player"+ row_id + 
								"$In$SelectPlayerType$Jersey$img_JerseyText" + "*TEXTURE*IMAGE SET "+ colors_path + "BLACK" + RugbyUtil.PNG_EXTENSION + "\0");
						}else {
							print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player"+ row_id + 
								"$In$SelectPlayerType$Jersey$img_JerseyText" + "*TEXTURE*IMAGE SET "+ colors_path + "WHITE" + RugbyUtil.PNG_EXTENSION + "\0");
						}
						
						if(row_id == 1) {
							print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
									"$In$SelectPlayerType$GK_Jersey$img_GK_JerseyText$txt_Number*GEOM*TEXT SET " + hs.getJersey_number() + "\0");
						}else {
							print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
									"$In$SelectPlayerType$Jersey$img_JerseyText$txt_Number*GEOM*TEXT SET " + hs.getJersey_number() + "\0");
						}
						
						
						break;
					case "WITH_IMAGE":
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
								"$In$SelectPlayerType*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player"+ row_id + 
								"$In$SelectPlayerType$ImageAll$img_PlayerImage" + "*TEXTURE*IMAGE SET "+ photos_path + match.getHomeTeam().getTeamName4().toUpperCase() + 
									"//" + hs.getPhoto() + RugbyUtil.PNG_EXTENSION + "\0");
						
						break;
					}
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
							"$In$txt_PlayerName*GEOM*TEXT SET " + hs.getTicker_name().toUpperCase() + "\0");
					
					TimeUnit.MILLISECONDS.sleep(l);
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
							"$Out$Dehighlight$NameGrp$SelectCard*FUNCTION*Omo*vis_con SET " + "0" + "\0");
					
				}
				
				for(Formation form : formation) {
					if(form.getFormId() == match.getHomeTeamFormationId()) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$txt_Formation*GEOM*TEXT SET " + form.getFormDescription() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player1*TRANSFORMATION*POSITION*X SET " + form.getFormOrds1X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player1*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds1Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player2*TRANSFORMATION*POSITION*X SET " + form.getFormOrds2X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player2*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds2Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player3*TRANSFORMATION*POSITION*X SET " + form.getFormOrds3X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player3*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds3Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player4*TRANSFORMATION*POSITION*X SET " + form.getFormOrds4X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player4*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds4Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player5*TRANSFORMATION*POSITION*X SET " + form.getFormOrds5X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player5*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds5Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player6*TRANSFORMATION*POSITION*X SET " + form.getFormOrds6X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player6*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds6Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player7*TRANSFORMATION*POSITION*X SET " + form.getFormOrds7X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player7*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds7Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player8*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds8Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player9*TRANSFORMATION*POSITION*X SET " + form.getFormOrds9X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player9*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds9Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player10*TRANSFORMATION*POSITION*X SET " + form.getFormOrds10X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player10*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds10Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player11*TRANSFORMATION*POSITION*X SET " + form.getFormOrds11X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player11*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds11Y() + "\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player1*TRANSFORMATION*POSITION*X SET " + form.getFormOrds1X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player1*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds1Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player2*TRANSFORMATION*POSITION*X SET " + form.getFormOrds2X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player2*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds2Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player3*TRANSFORMATION*POSITION*X SET " + form.getFormOrds3X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player3*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds3Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player4*TRANSFORMATION*POSITION*X SET " + form.getFormOrds4X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player4*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds4Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player5*TRANSFORMATION*POSITION*X SET " + form.getFormOrds5X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player5*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds5Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player6*TRANSFORMATION*POSITION*X SET " + form.getFormOrds6X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player6*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds6Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player7*TRANSFORMATION*POSITION*X SET " + form.getFormOrds7X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player7*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds7Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player8*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds8Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player9*TRANSFORMATION*POSITION*X SET " + form.getFormOrds9X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player9*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds9Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player10*TRANSFORMATION*POSITION*X SET " + form.getFormOrds10X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player10*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds10Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player11*TRANSFORMATION*POSITION*X SET " + form.getFormOrds11X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player11*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds11Y() + "\0");
					}
				}
				
				for(Player hsub : match.getHomeSubstitutes()) {
					row_id_sub = row_id_sub + 1;
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench"
							+ "*FUNCTION*Grid*num_row SET " + row_id_sub + "\0");
//					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*PlayerIn" + row_id + " SHOW 0.0 \0");
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
							"$Dehighlight$NumberGrp$txt_Number*GEOM*TEXT SET " + hsub.getJersey_number() + "\0");
					
					if(hsub.getSurname() != null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$NameAll$txt_FirstName*GEOM*TEXT SET " + hsub.getFirstname().toUpperCase() + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$NameAll$txt_LastName*GEOM*TEXT SET " + hsub.getSurname().toUpperCase() + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$NameAll$txt_FirstName*GEOM*TEXT SET " + hsub.getFull_name().toUpperCase() + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$NameAll$txt_LastName*GEOM*TEXT SET " + "" + "\0");
					}
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
							"$Dehighlight$NameGrp$SelectCard*FUNCTION*Omo*vis_con SET " + "0" + "\0");
					
					if(hsub.getCaptainGoalKeeper().equalsIgnoreCase(RugbyUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
				}
				
				
				
			//-------------------------------------------------------------------------------------------------------------------------------------------------
				
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$txt_TeamName*GEOM*TEXT SET " + 
						match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$SubHeadGrp$txt_SubHead1*GEOM*TEXT SET " + 
						"STARTING XI" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$SubHeadGrp$txt_SubHead2*GEOM*TEXT SET " + 
						"SUBSTITUTES" + "\0");
				
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$LogoGrp$LogoImageGrp1$img_BadgesBW"
						+ "*TEXTURE*IMAGE SET "+ logo_bw_path + team.get(match.getAwayTeamId() -1).getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$LogoGrp$LogoImageGrp4$img_Badges"
						+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(match.getAwayTeamId() -1).getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$LogoGrp$LogoImageGrp2$img_BadgesOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + team.get(match.getAwayTeamId() -1).getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$LogoGrp$LogoImageGrp3$img_BadgesOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + team.get(match.getAwayTeamId() -1).getTeamName4().toLowerCase() + "\0");
				
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
						colors_path + match.getAwayTeamJerseyColor() + RugbyUtil.PNG_EXTENSION + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player1"
						+ "$In$SelectPlayerType$GK_Jersey$img_GK_Jersey*TEXTURE*IMAGE SET "+ colors_path + match.getAwayTeamGKJerseyColor().toUpperCase() + RugbyUtil.PNG_EXTENSION + "\0");
				
				if(team.get(match.getAwayTeamId()-1).getTeamCoach() == null) {
					if(team.get(match.getAwayTeamId()-1).getTeamAssistantCoach() == null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$txt_CoachText*GEOM*TEXT SET " + 
								"" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_FirstName*GEOM*TEXT SET " + 
								"" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_LastName*GEOM*TEXT SET " + 
								"" + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$txt_CoachText*GEOM*TEXT SET " + 
								"ASST. COACH" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_FirstName*GEOM*TEXT SET " + 
								"" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_LastName*GEOM*TEXT SET " + 
								team.get(match.getAwayTeamId()-1).getTeamAssistantCoach() + "\0");
					}
					
				}else {
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$txt_CoachText*GEOM*TEXT SET " + 
							"COACH" + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_FirstName*GEOM*TEXT SET " + 
							"" + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_LastName*GEOM*TEXT SET " + 
							team.get(match.getAwayTeamId()-1).getTeamCoach() + "\0");
					
				}
				
				row_id = 0;
				for(Player as : match.getAwaySquad()) {
					row_id = row_id + 1;
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp"
							+ "*FUNCTION*Grid*num_row SET " + row_id + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
							"$Out$Dehighlight$NumberGrp$txt_Number*GEOM*TEXT SET " + as.getJersey_number() + "\0");
					
					if(as.getSurname() != null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$NameAll$txt_FirstName*GEOM*TEXT SET " + as.getFirstname().toUpperCase() + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$NameAll$txt_LastName*GEOM*TEXT SET " + as.getSurname().toUpperCase() + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$NameAll$txt_FirstName*GEOM*TEXT SET " + as.getFull_name().toUpperCase() + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$NameAll$txt_LastName*GEOM*TEXT SET " + "" + "\0");
					}
					
					
					if(as.getCaptainGoalKeeper().equalsIgnoreCase(RugbyUtil.CAPTAIN)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectCaptain*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(as.getCaptainGoalKeeper().equalsIgnoreCase(RugbyUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectCaptain*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(as.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectCaptain*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectCaptain*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
					//FORMATION
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player" + row_id + 
							"$Out$In$Dehighlight$Radikal-Bold*GEOM*TEXT SET " + as.getJersey_number() + "\0");
					
					switch(Type.toUpperCase()) {
					case "WITHOUT_IMAGE":
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
								"$In$SelectPlayerType*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player"+ row_id + 
							"$In$SelectPlayerType$Jersey$img_Jersey"+ "*TEXTURE*IMAGE SET "+ colors_path + match.getAwayTeamJerseyColor().toUpperCase() + RugbyUtil.PNG_EXTENSION + "\0");
						
						if(match.getAwayTeamJerseyColor().equalsIgnoreCase("WHITE")) {
							print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player"+ row_id + 
								"$In$SelectPlayerType$Jersey$img_JerseyText" + "*TEXTURE*IMAGE SET "+ colors_path + "BLACK" + RugbyUtil.PNG_EXTENSION + "\0");
						}else {
							print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player"+ row_id + 
								"$In$SelectPlayerType$Jersey$img_JerseyText" + "*TEXTURE*IMAGE SET "+ colors_path + "WHITE" + RugbyUtil.PNG_EXTENSION + "\0");
						}
						
						if(row_id == 1) {
							print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
									"$In$SelectPlayerType$GK_Jersey$img_GK_JerseyText$txt_Number*GEOM*TEXT SET " + as.getJersey_number() + "\0");
						}else {
							print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
									"$In$SelectPlayerType$Jersey$img_JerseyText$txt_Number*GEOM*TEXT SET " + as.getJersey_number() + "\0");
						}
						break;
					case "WITH_IMAGE":
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
								"$In$SelectPlayerType*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player"+ row_id + 
								"$In$SelectPlayerType$ImageAll$img_PlayerImage" + "*TEXTURE*IMAGE SET "+ photos_path + match.getAwayTeam().getTeamName4().toUpperCase() + 
									"//" + as.getPhoto() + RugbyUtil.PNG_EXTENSION + "\0");
						
						break;
					}
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
							"$In$txt_PlayerName*GEOM*TEXT SET " + as.getTicker_name().toUpperCase() + "\0");
					
					TimeUnit.MILLISECONDS.sleep(l);
					
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
							"$Out$Dehighlight$NameGrp$SelectCard*FUNCTION*Omo*vis_con SET " + "0" + "\0");
					
					
				}
				
				for(Formation form : formation) {
					if(form.getFormId() == match.getAwayTeamFormationId()) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$txt_Formation*GEOM*TEXT SET " + form.getFormDescription() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player1*TRANSFORMATION*POSITION*X SET " + form.getFormOrds1X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player1*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds1Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player2*TRANSFORMATION*POSITION*X SET " + form.getFormOrds2X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player2*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds2Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player3*TRANSFORMATION*POSITION*X SET " + form.getFormOrds3X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player3*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds3Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player4*TRANSFORMATION*POSITION*X SET " + form.getFormOrds4X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player4*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds4Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player5*TRANSFORMATION*POSITION*X SET " + form.getFormOrds5X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player5*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds5Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player6*TRANSFORMATION*POSITION*X SET " + form.getFormOrds6X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player6*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds6Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player7*TRANSFORMATION*POSITION*X SET " + form.getFormOrds7X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player7*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds7Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player8*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds8Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player9*TRANSFORMATION*POSITION*X SET " + form.getFormOrds9X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player9*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds9Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player10*TRANSFORMATION*POSITION*X SET " + form.getFormOrds10X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player10*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds10Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player11*TRANSFORMATION*POSITION*X SET " + form.getFormOrds11X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player11*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds11Y() + "\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player1*TRANSFORMATION*POSITION*X SET " + form.getFormOrds1X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player1*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds1Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player2*TRANSFORMATION*POSITION*X SET " + form.getFormOrds2X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player2*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds2Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player3*TRANSFORMATION*POSITION*X SET " + form.getFormOrds3X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player3*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds3Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player4*TRANSFORMATION*POSITION*X SET " + form.getFormOrds4X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player4*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds4Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player5*TRANSFORMATION*POSITION*X SET " + form.getFormOrds5X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player5*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds5Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player6*TRANSFORMATION*POSITION*X SET " + form.getFormOrds6X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player6*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds6Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player7*TRANSFORMATION*POSITION*X SET " + form.getFormOrds7X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player7*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds7Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player8*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds8Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player9*TRANSFORMATION*POSITION*X SET " + form.getFormOrds9X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player9*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds9Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player10*TRANSFORMATION*POSITION*X SET " + form.getFormOrds10X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player10*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds10Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player11*TRANSFORMATION*POSITION*X SET " + form.getFormOrds11X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player11*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds11Y() + "\0");
					}
				}
				
				row_id_sub = 0;
				for(Player asub : match.getAwaySubstitutes()) {
					row_id_sub = row_id_sub + 1;
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$Bench"
							+ "*FUNCTION*Grid*num_row SET " + row_id_sub + "\0");
//					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*PlayerIn" + row_id + " SHOW 0.0 \0");
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$Bench$Row" + row_id_sub + 
							"$Dehighlight$NumberGrp$txt_Number*GEOM*TEXT SET " + asub.getJersey_number() + "\0");
					
					if(asub.getSurname() != null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$NameAll$txt_FirstName*GEOM*TEXT SET " + asub.getFirstname().toUpperCase() + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$NameAll$txt_LastName*GEOM*TEXT SET " + asub.getSurname().toUpperCase() + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$NameAll$txt_FirstName*GEOM*TEXT SET " + asub.getFull_name().toUpperCase() + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$NameAll$txt_LastName*GEOM*TEXT SET " + "" + "\0");
					}
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$Bench$Row" + row_id_sub + 
							"$Dehighlight$NameGrp$SelectCard*FUNCTION*Omo*vis_con SET " + "0" + "\0");
					
					if(asub.getCaptainGoalKeeper().equalsIgnoreCase(RugbyUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
				}
				
			}else if(TeamId == match.getAwayTeamId()) {
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$txt_TeamName*GEOM*TEXT SET " + 
						team.get(TeamId-1).getTeamName1().toUpperCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$SubHeadGrp$txt_SubHead1*GEOM*TEXT SET " + 
						"STARTING XI" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$SubHeadGrp$txt_SubHead2*GEOM*TEXT SET " + 
						"SUBSTITUTES" + "\0");
				
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$LogoGrp$LogoImageGrp1$img_BadgesBW"
						+ "*TEXTURE*IMAGE SET "+ logo_bw_path + team.get(match.getAwayTeamId() -1).getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$LogoGrp$LogoImageGrp4$img_Badges"
						+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(match.getAwayTeamId() -1).getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$LogoGrp$LogoImageGrp2$img_BadgesOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + team.get(match.getAwayTeamId() -1).getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$LogoGrp$LogoImageGrp3$img_BadgesOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + team.get(match.getAwayTeamId() -1).getTeamName4().toLowerCase() + "\0");
				
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
						colors_path + match.getAwayTeamJerseyColor() + RugbyUtil.PNG_EXTENSION + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player1"
						+ "$In$SelectPlayerType$GK_Jersey$img_GK_Jersey*TEXTURE*IMAGE SET "+ colors_path + match.getAwayTeamGKJerseyColor().toUpperCase() + RugbyUtil.PNG_EXTENSION + "\0");
				
				if(team.get(TeamId-1).getTeamCoach() == null) {
					if(team.get(TeamId-1).getTeamAssistantCoach() == null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$txt_CoachText*GEOM*TEXT SET " + 
								"" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_FirstName*GEOM*TEXT SET " + 
								"" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_LastName*GEOM*TEXT SET " + 
								"" + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$txt_CoachText*GEOM*TEXT SET " + 
								"ASST. COACH" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_FirstName*GEOM*TEXT SET " + 
								"" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_LastName*GEOM*TEXT SET " + 
								team.get(TeamId-1).getTeamAssistantCoach() + "\0");
					}
					
				}else {
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$txt_CoachText*GEOM*TEXT SET " + 
							"COACH" + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_FirstName*GEOM*TEXT SET " + 
							"" + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_LastName*GEOM*TEXT SET " + 
							team.get(TeamId-1).getTeamCoach() + "\0");
					
				}
				row_id = 0;
				for(Player as : match.getAwaySquad()) {
					row_id = row_id + 1;
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp"
							+ "*FUNCTION*Grid*num_row SET " + row_id + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
							"$Out$Dehighlight$NumberGrp$txt_Number*GEOM*TEXT SET " + as.getJersey_number() + "\0");
					
					if(as.getSurname() != null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$NameAll$txt_FirstName*GEOM*TEXT SET " + as.getFirstname().toUpperCase() + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$NameAll$txt_LastName*GEOM*TEXT SET " + as.getSurname().toUpperCase() + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$NameAll$txt_FirstName*GEOM*TEXT SET " + as.getTicker_name().toUpperCase() + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$NameAll$txt_LastName*GEOM*TEXT SET " + "" + "\0");
					}
					
					if(as.getCaptainGoalKeeper().equalsIgnoreCase(RugbyUtil.CAPTAIN)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectCaptain*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(as.getCaptainGoalKeeper().equalsIgnoreCase(RugbyUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectCaptain*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(as.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectCaptain*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectCaptain*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
					
					//FORMATION
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player" + row_id + 
							"$Out$In$Dehighlight$Radikal-Bold*GEOM*TEXT SET " + as.getJersey_number() + "\0");
					
					switch(Type.toUpperCase()) {
					case "WITHOUT_IMAGE":
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
								"$In$SelectPlayerType*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player"+ row_id + 
							"$In$SelectPlayerType$Jersey$img_Jersey"+ "*TEXTURE*IMAGE SET "+ colors_path + match.getAwayTeamJerseyColor().toUpperCase() + RugbyUtil.PNG_EXTENSION + "\0");
						
						if(match.getAwayTeamJerseyColor().equalsIgnoreCase("WHITE")) {
							print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player"+ row_id + 
								"$In$SelectPlayerType$Jersey$img_JerseyText" + "*TEXTURE*IMAGE SET "+ colors_path + "BLACK" + RugbyUtil.PNG_EXTENSION + "\0");
						}else {
							print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player"+ row_id + 
								"$In$SelectPlayerType$Jersey$img_JerseyText" + "*TEXTURE*IMAGE SET "+ colors_path + "WHITE" + RugbyUtil.PNG_EXTENSION + "\0");
						}
						
						if(row_id == 1) {
							print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
									"$In$SelectPlayerType$GK_Jersey$img_GK_JerseyText$txt_Number*GEOM*TEXT SET " + as.getJersey_number() + "\0");
						}else {
							print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
									"$In$SelectPlayerType$Jersey$img_JerseyText$txt_Number*GEOM*TEXT SET " + as.getJersey_number() + "\0");
						}
						break;
					case "WITH_IMAGE":
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
								"$In$SelectPlayerType*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player"+ row_id + 
								"$In$SelectPlayerType$ImageAll$img_PlayerImage" + "*TEXTURE*IMAGE SET "+ photos_path + match.getAwayTeam().getTeamName4().toUpperCase() + 
									"//" + as.getPhoto() + RugbyUtil.PNG_EXTENSION + "\0");
						
						break;
					}
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
							"$In$txt_PlayerName*GEOM*TEXT SET " + as.getTicker_name().toUpperCase() + "\0");
					
					TimeUnit.MILLISECONDS.sleep(l);
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$LineUp$Row" + row_id + 
							"$Out$Dehighlight$NameGrp$SelectCard*FUNCTION*Omo*vis_con SET " + "0" + "\0");
					
					
				}
				
				for(Formation form : formation) {
					if(form.getFormId() == match.getAwayTeamFormationId()) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$txt_Formation*GEOM*TEXT SET " + form.getFormDescription() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player1*TRANSFORMATION*POSITION*X SET " + form.getFormOrds1X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player1*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds1Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player2*TRANSFORMATION*POSITION*X SET " + form.getFormOrds2X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player2*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds2Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player3*TRANSFORMATION*POSITION*X SET " + form.getFormOrds3X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player3*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds3Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player4*TRANSFORMATION*POSITION*X SET " + form.getFormOrds4X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player4*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds4Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player5*TRANSFORMATION*POSITION*X SET " + form.getFormOrds5X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player5*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds5Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player6*TRANSFORMATION*POSITION*X SET " + form.getFormOrds6X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player6*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds6Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player7*TRANSFORMATION*POSITION*X SET " + form.getFormOrds7X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player7*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds7Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player8*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds8Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player9*TRANSFORMATION*POSITION*X SET " + form.getFormOrds9X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player9*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds9Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player10*TRANSFORMATION*POSITION*X SET " + form.getFormOrds10X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player10*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds10Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player11*TRANSFORMATION*POSITION*X SET " + form.getFormOrds11X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticallNumber$Player11*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds11Y() + "\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player1*TRANSFORMATION*POSITION*X SET " + form.getFormOrds1X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player1*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds1Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player2*TRANSFORMATION*POSITION*X SET " + form.getFormOrds2X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player2*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds2Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player3*TRANSFORMATION*POSITION*X SET " + form.getFormOrds3X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player3*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds3Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player4*TRANSFORMATION*POSITION*X SET " + form.getFormOrds4X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player4*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds4Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player5*TRANSFORMATION*POSITION*X SET " + form.getFormOrds5X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player5*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds5Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player6*TRANSFORMATION*POSITION*X SET " + form.getFormOrds6X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player6*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds6Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player7*TRANSFORMATION*POSITION*X SET " + form.getFormOrds7X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player7*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds7Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player8*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds8Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player9*TRANSFORMATION*POSITION*X SET " + form.getFormOrds9X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player9*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds9Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player10*TRANSFORMATION*POSITION*X SET " + form.getFormOrds10X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player10*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds10Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player11*TRANSFORMATION*POSITION*X SET " + form.getFormOrds11X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$TacticalAllGrp$TacticalImage$Player11*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds11Y() + "\0");
					}
				}
				
				row_id_sub = 0;
				for(Player asub : match.getAwaySubstitutes()) {
					row_id_sub = row_id_sub + 1;
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench"
							+ "*FUNCTION*Grid*num_row SET " + row_id_sub + "\0");

					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
							"$Dehighlight$NumberGrp$txt_Number*GEOM*TEXT SET " + asub.getJersey_number() + "\0");
					
					if(asub.getSurname() != null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$NameAll$txt_FirstName*GEOM*TEXT SET " + asub.getFirstname().toUpperCase() + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$NameAll$txt_LastName*GEOM*TEXT SET " + asub.getSurname().toUpperCase() + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$NameAll$txt_FirstName*GEOM*TEXT SET " + asub.getFull_name().toUpperCase() + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$NameAll$txt_LastName*GEOM*TEXT SET " + "" + "\0");
					}
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
							"$Dehighlight$NameGrp$SelectCard*FUNCTION*Omo*vis_con SET " + "0" + "\0");
					
					if(asub.getCaptainGoalKeeper().equalsIgnoreCase(RugbyUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team1$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
				}
				
				
				//---------------------------------------------------------------------------------------------------------------------------------------------------
				
				
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$txt_TeamName*GEOM*TEXT SET " + 
						match.getHomeTeam().getTeamName1().toUpperCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$SubHeadGrp$txt_SubHead1*GEOM*TEXT SET " + 
						"STARTING XI" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$SubHeadGrp$txt_SubHead2*GEOM*TEXT SET " + 
						"SUBSTITUTES" + "\0");
				
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$LogoGrp$LogoImageGrp1$img_BadgesBW"
						+ "*TEXTURE*IMAGE SET "+ logo_bw_path + team.get(match.getHomeTeamId() -1).getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$LogoGrp$LogoImageGrp4$img_Badges"
						+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(match.getHomeTeamId() -1).getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$LogoGrp$LogoImageGrp2$img_BadgesOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + team.get(match.getHomeTeamId() -1).getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$LogoGrp$LogoImageGrp3$img_BadgesOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + team.get(match.getHomeTeamId() -1).getTeamName4().toLowerCase() + "\0");
				
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$img_TeamColour*TEXTURE*IMAGE SET " + 
						colors_path + match.getHomeTeamJerseyColor() + RugbyUtil.PNG_EXTENSION + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player1"
						+ "$In$SelectPlayerType$GK_Jersey$img_GK_Jersey*TEXTURE*IMAGE SET "+ colors_path + match.getHomeTeamGKJerseyColor().toUpperCase() + RugbyUtil.PNG_EXTENSION + "\0");
				
				if(team.get(match.getHomeTeamId()-1).getTeamCoach() == null) {
					if(team.get(match.getHomeTeamId()-1).getTeamAssistantCoach() == null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$txt_CoachText*GEOM*TEXT SET " + 
								"" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_FirstName*GEOM*TEXT SET " + 
								"" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_LastName*GEOM*TEXT SET " + 
								"" + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$txt_CoachText*GEOM*TEXT SET " + 
								"ASST. COACH" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_FirstName*GEOM*TEXT SET " + 
								"" + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_LastName*GEOM*TEXT SET " + 
								team.get(match.getHomeTeamId()-1).getTeamAssistantCoach() + "\0");
					}
					
				}else {
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$txt_CoachText*GEOM*TEXT SET " + 
							"COACH" + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_FirstName*GEOM*TEXT SET " + 
							"" + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$CoachGrp$CoachNameGrp$MaxSize$NameAll$txt_LastName*GEOM*TEXT SET " + 
							team.get(match.getHomeTeamId()-1).getTeamCoach() + "\0");
					
				}
				
				row_id = 0;
				for(Player hs : match.getHomeSquad()) {
					row_id = row_id + 1;
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp"
							+ "*FUNCTION*Grid*num_row SET " + row_id + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
							"$Out$Dehighlight$NumberGrp$txt_Number*GEOM*TEXT SET " + hs.getJersey_number() + "\0");
					
					if(hs.getSurname() != null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$NameAll$txt_FirstName*GEOM*TEXT SET " + hs.getFirstname().toUpperCase() + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$NameAll$txt_LastName*GEOM*TEXT SET " + hs.getSurname().toUpperCase() + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$NameAll$txt_FirstName*GEOM*TEXT SET " + hs.getFull_name().toUpperCase() + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$NameAll$txt_LastName*GEOM*TEXT SET " + "" + "\0");
					}
					
					if(hs.getCaptainGoalKeeper().equalsIgnoreCase(RugbyUtil.CAPTAIN)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectCaptain*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase(RugbyUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectCaptain*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
					}else if(hs.getCaptainGoalKeeper().equalsIgnoreCase("CAPTAIN_GOAL_KEEPER")) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectCaptain*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
								"$Out$Dehighlight$NameGrp$SelectCaptain*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
					
					//FORMATION
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player" + row_id + 
							"$Out$In$Dehighlight$Radikal-Bold*GEOM*TEXT SET " + hs.getJersey_number() + "\0");
					
					switch(Type.toUpperCase()) {
					case "WITHOUT_IMAGE":
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
								"$In$SelectPlayerType*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player"+ row_id + 
							"$In$SelectPlayerType$Jersey$img_Jersey"+ "*TEXTURE*IMAGE SET "+ colors_path + match.getHomeTeamJerseyColor().toUpperCase() + RugbyUtil.PNG_EXTENSION + "\0");
						
						if(match.getHomeTeamJerseyColor().equalsIgnoreCase("WHITE")) {
							print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player"+ row_id + 
								"$In$SelectPlayerType$Jersey$img_JerseyText" + "*TEXTURE*IMAGE SET "+ colors_path + "BLACK" + RugbyUtil.PNG_EXTENSION + "\0");
						}else {
							print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player"+ row_id + 
								"$In$SelectPlayerType$Jersey$img_JerseyText" + "*TEXTURE*IMAGE SET "+ colors_path + "WHITE" + RugbyUtil.PNG_EXTENSION + "\0");
						}
						
						if(row_id == 1) {
							print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
									"$In$SelectPlayerType$GK_Jersey$img_GK_JerseyText$txt_Number*GEOM*TEXT SET " + hs.getJersey_number() + "\0");
						}else {
							print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
									"$In$SelectPlayerType$Jersey$img_JerseyText$txt_Number*GEOM*TEXT SET " + hs.getJersey_number() + "\0");
						}
						break;
					case "WITH_IMAGE":
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
								"$In$SelectPlayerType*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player"+ row_id + 
								"$In$SelectPlayerType$ImageAll$img_PlayerImage" + "*TEXTURE*IMAGE SET "+ photos_path + match.getHomeTeam().getTeamName4().toUpperCase() + 
									"//" + hs.getPhoto() + RugbyUtil.PNG_EXTENSION + "\0");
						
						break;
					}
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player" + row_id + 
							"$In$txt_PlayerName*GEOM*TEXT SET " + hs.getTicker_name().toUpperCase() + "\0");
					
					TimeUnit.MILLISECONDS.sleep(l);
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$LineUp$Row" + row_id + 
							"$Out$Dehighlight$NameGrp$SelectCard*FUNCTION*Omo*vis_con SET " + "0" + "\0");
					
					
				}
				
				for(Formation form : formation) {
					if(form.getFormId() == match.getHomeTeamFormationId()) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$txt_Formation*GEOM*TEXT SET " + form.getFormDescription() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player1*TRANSFORMATION*POSITION*X SET " + form.getFormOrds1X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player1*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds1Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player2*TRANSFORMATION*POSITION*X SET " + form.getFormOrds2X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player2*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds2Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player3*TRANSFORMATION*POSITION*X SET " + form.getFormOrds3X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player3*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds3Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player4*TRANSFORMATION*POSITION*X SET " + form.getFormOrds4X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player4*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds4Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player5*TRANSFORMATION*POSITION*X SET " + form.getFormOrds5X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player5*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds5Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player6*TRANSFORMATION*POSITION*X SET " + form.getFormOrds6X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player6*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds6Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player7*TRANSFORMATION*POSITION*X SET " + form.getFormOrds7X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player7*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds7Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player8*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds8Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player9*TRANSFORMATION*POSITION*X SET " + form.getFormOrds9X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player9*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds9Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player10*TRANSFORMATION*POSITION*X SET " + form.getFormOrds10X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player10*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds10Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player11*TRANSFORMATION*POSITION*X SET " + form.getFormOrds11X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticallNumber$Player11*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds11Y() + "\0");
						
						TimeUnit.MILLISECONDS.sleep(l);
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player1*TRANSFORMATION*POSITION*X SET " + form.getFormOrds1X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player1*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds1Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player2*TRANSFORMATION*POSITION*X SET " + form.getFormOrds2X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player2*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds2Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player3*TRANSFORMATION*POSITION*X SET " + form.getFormOrds3X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player3*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds3Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player4*TRANSFORMATION*POSITION*X SET " + form.getFormOrds4X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player4*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds4Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player5*TRANSFORMATION*POSITION*X SET " + form.getFormOrds5X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player5*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds5Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player6*TRANSFORMATION*POSITION*X SET " + form.getFormOrds6X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player6*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds6Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player7*TRANSFORMATION*POSITION*X SET " + form.getFormOrds7X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player7*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds7Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player8*TRANSFORMATION*POSITION*X SET " + form.getFormOrds8X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player8*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds8Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player9*TRANSFORMATION*POSITION*X SET " + form.getFormOrds9X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player9*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds9Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player10*TRANSFORMATION*POSITION*X SET " + form.getFormOrds10X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player10*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds10Y() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player11*TRANSFORMATION*POSITION*X SET " + form.getFormOrds11X() + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$TacticalAllGrp$TacticalImage$Player11*TRANSFORMATION*POSITION*Y SET " + form.getFormOrds11Y() + "\0");
					}
				}
				
				row_id_sub = 0;
				for(Player hsub : match.getHomeSubstitutes()) {
					row_id_sub = row_id_sub + 1;
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$Bench"
							+ "*FUNCTION*Grid*num_row SET " + row_id_sub + "\0");
//					print_writer.println("-1 RENDERER*STAGE*DIRECTOR*PlayerIn" + row_id + " SHOW 0.0 \0");
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$Bench$Row" + row_id_sub + 
							"$Dehighlight$NumberGrp$txt_Number*GEOM*TEXT SET " + hsub.getJersey_number() + "\0");
					
					if(hsub.getSurname() != null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$NameAll$txt_FirstName*GEOM*TEXT SET " + hsub.getFirstname().toUpperCase() + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$NameAll$txt_LastName*GEOM*TEXT SET " + hsub.getSurname().toUpperCase() + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$NameAll$txt_FirstName*GEOM*TEXT SET " + hsub.getFull_name().toUpperCase() + "\0");
						
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$NameAll$txt_LastName*GEOM*TEXT SET " + "" + "\0");
					}
					
					print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$Bench$Row" + row_id_sub + 
							"$Dehighlight$NameGrp$SelectCard*FUNCTION*Omo*vis_con SET " + "0" + "\0");
					
					if(hsub.getCaptainGoalKeeper().equalsIgnoreCase(RugbyUtil.GOAL_KEEPER)) {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "1" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All_FullFRames$TeamsAll$Team2$AllData$DataOut$Bench$Row" + row_id_sub + 
								"$Dehighlight$NameGrp$SelectGoalKeeper*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
				}
			}
		}
		print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 FF_In 2.000 LineUp$Team1$DataIn 2.520 \0");
	}
	public void populateMatchDoublePromo(Socket session_socket,String viz_scene,String day,Match match,List<Fixture> fixture,List<Team> team,List<Ground> ground, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0 \0");
			
			int row_id = 1 ,l=4;
			String Date = "",grou = "";
			Calendar cal = Calendar.getInstance();
			
			if(day.toUpperCase().equalsIgnoreCase("TODAY")) {
				Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
				print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$txt_Header*GEOM*TEXT SET " + "TODAY'S MATCHES" + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			else if(day.toUpperCase().equalsIgnoreCase("TOMORROW")) {
				cal.add(Calendar.DATE, +1);
				Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
				print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$txt_Header*GEOM*TEXT SET " + "TOMORROW'S MATCHES" + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET " + match.getTournament().toUpperCase() + "\0");
			TimeUnit.MILLISECONDS.sleep(l);
			
			
			for(int i = 0; i <= fixture.size()-1; i++) {
				if(fixture.get(i).getDate().equalsIgnoreCase(Date)) {
					for(int j = 0; j <= ground.size()-1; j++) {
						if(ground.get(j).getGroundId() == Integer.valueOf(fixture.get(i).getVenue())) {
							grou = ground.get(j).getFullname();
						}
					}
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$BottomInfoGrp$txt_Info*GEOM*TEXT SET " + grou + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$GroupNameGrp$txt_GroupName*GEOM*TEXT SET " + 
							fixture.get(i).getGroupName() + "\0");
					
					if(day.toUpperCase().equalsIgnoreCase("TODAY")) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TimeGrp$txt_Time*GEOM*TEXT SET " + 
								" " + "\0");
					}
					else if(day.toUpperCase().equalsIgnoreCase("TOMORROW")) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TimeGrp$txt_Time*GEOM*TEXT SET " + 
								"KICK OFF AT " + fixture.get(i).getTime() + " LOCAL TIME" + "\0");
					}
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TeamGrp1$ImageGrp$LogoImageGrp1$img_BadgesBW"
							+ "*TEXTURE*IMAGE SET "+ logo_bw_path + team.get(fixture.get(i).getHometeamid() - 1).getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TeamGrp1$ImageGrp$LogoImageGrp2$img_BadgesOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + team.get(fixture.get(i).getHometeamid() - 1).getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TeamGrp1$ImageGrp$LogoImageGrp3$img_BadgesOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + team.get(fixture.get(i).getHometeamid() - 1).getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TeamGrp1$ImageGrp$LogoImageGrp4$img_Badges"
							+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(fixture.get(i).getHometeamid() - 1).getTeamName4().toLowerCase() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TeamGrp2$ImageGrp$LogoImageGrp1$img_BadgesBW"
							+ "*TEXTURE*IMAGE SET "+ logo_bw_path + team.get(fixture.get(i).getAwayteamid() - 1).getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TeamGrp2$ImageGrp$LogoImageGrp2$img_BadgesOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + team.get(fixture.get(i).getAwayteamid() - 1).getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TeamGrp2$ImageGrp$LogoImageGrp3$img_BadgesOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + team.get(fixture.get(i).getAwayteamid() - 1).getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TeamGrp2$ImageGrp$LogoImageGrp4$img_Badges"
							+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(fixture.get(i).getAwayteamid() - 1).getTeamName4().toLowerCase() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$SeparatorGrpGrp$txt_Separator*GEOM*TEXT SET " + "VS" + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TeamGrp1$TeamData1$TeamAll$Team$txt_TeamName*GEOM*TEXT SET " + 
							team.get(fixture.get(i).getHometeamid() - 1).getTeamName1().toUpperCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$DoubleMatchId$AllData$Group" + row_id + "$TeamGrp2$TeamData2$TeamAll$Team$txt_TeamName*GEOM*TEXT SET " + 
							team.get(fixture.get(i).getAwayteamid() - 1).getTeamName1().toUpperCase() + "\0");
					
					row_id = row_id +1;
				}
			}
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 FF_In 2.000 DoubleID_In 2.600 \0");
		}
	}
	public void populatePointsTable(Socket session_socket,String viz_sence_path,String Group,List<LeagueTeam> point_table, List<Team> team,String session_selected_broadcaster,Match match) throws InterruptedException, IOException 
	{		
		
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0 \0");
		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$BottomInfoGrp$txt_Info*GEOM*TEXT SET " + 
				"GROUP WINNER WILL QUALIFY FOR THE SEMI-FINALS" + "\0");
		
		int row_no=0,omo=0,l=4;
		String cout = "";
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$txt_Header*GEOM*TEXT SET " + "POINTS TABLE" + "\0");
		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET " + match.getTournament() + "\0");
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style*FUNCTION*Omo*vis_con SET " + "0" + "\0");
		TimeUnit.MILLISECONDS.sleep(l);
		
		if(Group.equalsIgnoreCase("LeagueTableA")) {
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$GroupHeadGrp1$txt_Group*GEOM*TEXT SET " + "GROUP A" + "\0");
		}else if(Group.equalsIgnoreCase("LeagueTableB")) {
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$GroupHeadGrp1$txt_Group*GEOM*TEXT SET " + "GROUP B" + "\0");
		}else if(Group.equalsIgnoreCase("LeagueTableC")) {
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$GroupHeadGrp1$txt_Group*GEOM*TEXT SET " + "GROUP C" + "\0");
		}else if(Group.equalsIgnoreCase("LeagueTableD")) {
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$GroupHeadGrp1$txt_Group*GEOM*TEXT SET " + "GROUP D" + "\0");
		}
		
		for(int i = 0; i <= point_table.size() - 1 ; i++) {
			row_no = row_no + 1;
			
			if(match.getHomeTeam().getTeamName2().equalsIgnoreCase(point_table.get(i).getTeamName()) || 
					match.getAwayTeam().getTeamName2().equalsIgnoreCase(point_table.get(i).getTeamName())){
				omo=1;
				cout="$Highlight";
			}else {
				omo=0;
				cout="$Dehighlight";
			}
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
					"$SelectType*FUNCTION*Omo*vis_con SET " + omo + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
					"$SelectType" + cout + "$Text$txt_Rank*GEOM*TEXT SET " + row_no + "\0");
			
			for(Team tm : team) {
				if(point_table.get(i).getTeamName().equalsIgnoreCase("GOA")) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
							"$SelectType" + cout + "$Text$img_TeamBadge*TEXTURE*IMAGE SET "+ logo_path + "fcg" + "\0");
					TimeUnit.MILLISECONDS.sleep(l);
					
					if(point_table.get(i).getQualifiedStatus().trim().equalsIgnoreCase("")) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + "FC GOA" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + "FC GOA (Q)" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					TimeUnit.MILLISECONDS.sleep(l);
					
				}else if(tm.getTeamName1().contains(point_table.get(i).getTeamName())) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
							"$SelectType" + cout + "$Text$img_TeamBadge*TEXTURE*IMAGE SET "+ logo_path + tm.getTeamName4().toLowerCase() + "\0");
					TimeUnit.MILLISECONDS.sleep(l);
					
					if(point_table.get(i).getQualifiedStatus().trim().equalsIgnoreCase("")) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + tm.getTeamName1().toUpperCase() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + tm.getTeamName1().toUpperCase() + " (Q)" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
					"$SelectType" + cout + "$Text$PointsData$txt_PlayedValue*GEOM*TEXT SET " + point_table.get(i).getPlayed() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
					"$SelectType" + cout + "$Text$PointsData$txt_WinValue*GEOM*TEXT SET " + point_table.get(i).getWon() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
					"$SelectType" + cout + "$Text$PointsData$txt_DrawValue*GEOM*TEXT SET " + point_table.get(i).getLost() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
					"$SelectType" + cout + "$Text$PointsData$txt_LostValue*GEOM*TEXT SET " + point_table.get(i).getDrawn() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
					"$SelectType" + cout + "$Text$PointsData$txt_GoalDifferenceValue*GEOM*TEXT SET " + point_table.get(i).getGD() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group1$PointsData1$Row" + row_no + 
					"$SelectType" + cout + "$Text$PointsData$txt_PointsValue*GEOM*TEXT SET " + point_table.get(i).getPoints() + "\0");

		}
//		print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_sence_path + " C:/Temp/Preview.png In 0.020 FF_In 2.000 PlayOffs_In 2.700 \0");
		print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_sence_path + " C:/Temp/Preview.png In 0.020 FF_In 2.000 PointsTable_In 2.580 \0");
		
	}
	public void populatePointsTableGrp(Socket session_socket,String viz_sence_path,String Group,List<LeagueTeam> point_table1,List<LeagueTeam> point_table2, List<Team> team,String session_selected_broadcaster,Match match) throws InterruptedException, IOException 
	{		
		
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0 \0");
		
		
		int row_no_1=0,row_no_2=0,omo=0,l=4;
		String cout = "";
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$txt_Header*GEOM*TEXT SET " + "GROUP STANDINGS" + "\0");
		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET " + match.getTournament() + "\0");
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style*FUNCTION*Omo*vis_con SET " + "1" + "\0");
		TimeUnit.MILLISECONDS.sleep(l);
		
		if(Group.equalsIgnoreCase("SemiFinal1")) {
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$GroupHeadGrp$txt_Group*GEOM*TEXT SET " + "GROUP A" + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$GroupHeadGrp$txt_Group*GEOM*TEXT SET " + "GROUP C" + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$BottomInfoGrp$txt_Info*GEOM*TEXT SET " + 
					"GROUP A WINNER WILL PLAY GROUP C WINNER IN THE 1st SEMI-FINAL" + "\0");
			
		}else if(Group.equalsIgnoreCase("SemiFinal2")) {
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$GroupHeadGrp$txt_Group*GEOM*TEXT SET " + "GROUP B" + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$GroupHeadGrp$txt_Group*GEOM*TEXT SET " + "GROUP D" + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$BottomInfoGrp$txt_Info*GEOM*TEXT SET " + 
					"GROUP B WINNER WILL PLAY GROUP D WINNER IN THE 2nd SEMI-FINAL" + "\0");
		}
		
		for(int i = 0; i <= point_table1.size() - 1 ; i++) {
			row_no_1 = row_no_1 + 1;
			
			if(match.getHomeTeam().getTeamName2().equalsIgnoreCase(point_table1.get(i).getTeamName()) || 
					match.getAwayTeam().getTeamName2().equalsIgnoreCase(point_table1.get(i).getTeamName())){
				omo=1;
				cout="$Highlight";
			}else {
				omo=0;
				cout="$Dehighlight";
			}
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType*FUNCTION*Omo*vis_con SET " + omo + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType" + cout + "$Text$txt_Rank*GEOM*TEXT SET " + row_no_1 + "\0");
			
			for(Team tm : team) {
				if(point_table1.get(i).getTeamName().equalsIgnoreCase("GOA")) {
					if(point_table1.get(i).getQualifiedStatus().trim().equalsIgnoreCase("")) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + "FC GOA" + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + "FC GOA (Q)" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					
					TimeUnit.MILLISECONDS.sleep(l);
					
				}else if(tm.getTeamName1().contains(point_table1.get(i).getTeamName())) {
					
					if(point_table1.get(i).getQualifiedStatus().trim().equalsIgnoreCase("")) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + tm.getTeamName1().toUpperCase() + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + tm.getTeamName1().toUpperCase() + " (Q)" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType" + cout + "$Text$PointsData$txt_PlayedValue*GEOM*TEXT SET " + point_table1.get(i).getPlayed() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType" + cout + "$Text$PointsData$txt_WinValue*GEOM*TEXT SET " + point_table1.get(i).getWon() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType" + cout + "$Text$PointsData$txt_DrawValue*GEOM*TEXT SET " + point_table1.get(i).getLost() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType" + cout + "$Text$PointsData$txt_LostValue*GEOM*TEXT SET " + point_table1.get(i).getDrawn() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType" + cout + "$Text$PointsData$txt_GoalDifferenceValue*GEOM*TEXT SET " + point_table1.get(i).getGD() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
					"$SelectType" + cout + "$Text$PointsData$txt_PointsValue*GEOM*TEXT SET " + point_table1.get(i).getPoints() + "\0");

		}
		
		for(int i = 0; i <= point_table2.size() - 1 ; i++) {
			row_no_2 = row_no_2 + 1;
			
			if(match.getHomeTeam().getTeamName2().equalsIgnoreCase(point_table2.get(i).getTeamName()) || 
					match.getAwayTeam().getTeamName2().equalsIgnoreCase(point_table2.get(i).getTeamName())){
				omo=1;
				cout="$Highlight";
			}else {
				omo=0;
				cout="$Dehighlight";
			}
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType*FUNCTION*Omo*vis_con SET " + omo + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType" + cout + "$Text$txt_Rank*GEOM*TEXT SET " + row_no_2 + "\0");
			
			for(Team tm : team) {
				if(point_table2.get(i).getTeamName().equalsIgnoreCase("GOA")) {
					if(point_table2.get(i).getQualifiedStatus().trim().equalsIgnoreCase("")) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + "FC GOA" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + "FC GOA (Q)" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					TimeUnit.MILLISECONDS.sleep(l);
					
				}else if(tm.getTeamName1().contains(point_table2.get(i).getTeamName())) {
					if(point_table2.get(i).getQualifiedStatus().trim().equalsIgnoreCase("")) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + tm.getTeamName1().toUpperCase() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
								"$SelectType" + cout + "$Text$txt_TeamName*GEOM*TEXT SET " + tm.getTeamName1().toUpperCase() + " (Q)" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType" + cout + "$Text$PointsData$txt_PlayedValue*GEOM*TEXT SET " + point_table2.get(i).getPlayed() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType" + cout + "$Text$PointsData$txt_WinValue*GEOM*TEXT SET " + point_table2.get(i).getWon() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType" + cout + "$Text$PointsData$txt_DrawValue*GEOM*TEXT SET " + point_table2.get(i).getLost() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType" + cout + "$Text$PointsData$txt_LostValue*GEOM*TEXT SET " + point_table2.get(i).getDrawn() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType" + cout + "$Text$PointsData$txt_GoalDifferenceValue*GEOM*TEXT SET " + point_table2.get(i).getGD() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
					"$SelectType" + cout + "$Text$PointsData$txt_PointsValue*GEOM*TEXT SET " + point_table2.get(i).getPoints() + "\0");

		}
		print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_sence_path + " C:/Temp/Preview.png In 0.020 FF_In 2.000 PointsTable_In 2.580 \0");
		
	}
	public void populateFixtures(Socket session_socket,String viz_sence_path,String Group,String header,List<Fixture> fixture,List<Team> team,List<Ground> ground,String session_selected_broadcaster,Match match) throws InterruptedException, IOException 
	{		
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0 \0");
//		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$BottomInfoGrp$txt_Info*GEOM*TEXT SET " + 
//				"GROUP WINNER WILL QUALIFY FOR THE SEMI-FINALS" + "\0");
		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$BottomInfoGrp*ACTIVE SET 0\0");
		int row_no=0,omo=0;
		String cout = "",match_name="",new_date="";
		
		String[] dateSuffix = {
				"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
				
				"th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
				
				"th", "st", "nd", "rd", "th", "th", "th", "th", "th","th",
				
				"th", "st"
		};
				  
		
		
		match_name = match.getMatchFileName().replace(".xml", "");
		
		switch(header.toUpperCase()) {
		case "FIXTURE":
			if(Group.equalsIgnoreCase("group A")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$txt_Header*GEOM*TEXT SET " + "FIXTURES - GROUP A" + "\0");
			}else if(Group.equalsIgnoreCase("group B")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$txt_Header*GEOM*TEXT SET " + "FIXTURES - GROUP B" + "\0");
			}else if(Group.equalsIgnoreCase("group C")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$txt_Header*GEOM*TEXT SET " + "FIXTURES - GROUP C" + "\0");
			}else if(Group.equalsIgnoreCase("group D")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$txt_Header*GEOM*TEXT SET " + "FIXTURES - GROUP D" + "\0");
			}
			break;
		case "RESULT":
			if(Group.equalsIgnoreCase("group A")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$txt_Header*GEOM*TEXT SET " + "BENGALURU FC" + "\0");
			}else if(Group.equalsIgnoreCase("group B")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$txt_Header*GEOM*TEXT SET " + "ODISHA FC" + "\0");
			}else if(Group.equalsIgnoreCase("group C")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$txt_Header*GEOM*TEXT SET " + "JAMSHEDPUR FC" + "\0");
			}else if(Group.equalsIgnoreCase("group D")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$txt_Header*GEOM*TEXT SET " + "NORTHEAST UNITED FC" + "\0");
			}
			break;
		}
		
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET " + "GROUP STAGE RESULTS" + "\0");
		
		for(int i=0;i<=fixture.size()-1;i++) {
			if(fixture.get(i).getGroupName().equalsIgnoreCase(Group.toUpperCase())) {
				row_no = row_no + 1;
				
				if(row_no <= 2) {
					new_date = fixture.get(i).getDate().split("-")[0];
					if(Integer.valueOf(new_date) < 10) {
						new_date = new_date.replaceFirst("0", "");
					}
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row1$DateGrp$txt_Date*GEOM*TEXT SET " + 
							new_date + dateSuffix[Integer.valueOf(new_date)] + " MAY" + "\0");
					
					if(match_name.equalsIgnoreCase(fixture.get(i).getMatchfilename())) {
						omo=1;
						cout="$Highlight";
					}else {
						omo=0;
						cout="$Dehighlight";
					}
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row1$Grp" + row_no + 
							"$SelectMatchType*FUNCTION*Omo*vis_con SET " + omo + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row1$Grp" + row_no + "$SelectMatchType" + cout
							+ "$Team1$txt_TeamName1*GEOM*TEXT SET " + team.get(fixture.get(i).getHometeamid()-1).getTeamName1() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row1$Grp" + row_no + "$SelectMatchType" + cout
							+ "$Team1$txt_TeamName2*GEOM*TEXT SET " + team.get(fixture.get(i).getAwayteamid()-1).getTeamName1() + "\0");
					
					if(fixture.get(i).getMargin() == null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row1$Grp" + row_no + "$SelectMatchType" + cout
								+ "$Team1$txt_Separator*GEOM*TEXT SET " + "VS" + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row1$Grp" + row_no + "$SelectMatchType" + cout
								+ "$Team1$txt_Separator*GEOM*TEXT SET " + fixture.get(i).getMargin() + "\0");
					}
					
					
					
				}else if(row_no > 2 && row_no <= 4) {
					new_date = fixture.get(i).getDate().split("-")[0];
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row2$DateGrp$txt_Date*GEOM*TEXT SET " + 
							new_date + dateSuffix[Integer.valueOf(new_date)] + " MAY" + "\0");
					
					if(match_name.equalsIgnoreCase(fixture.get(i).getMatchfilename())) {
						omo=1;
						cout="$Highlight";
					}else {
						omo=0;
						cout="$Dehighlight";
					}
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row2$Grp" + row_no + 
							"$SelectMatchType*FUNCTION*Omo*vis_con SET " + omo + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row2$Grp" + row_no + "$SelectMatchType" + cout
							+ "$Team1$txt_TeamName1*GEOM*TEXT SET " + team.get(fixture.get(i).getHometeamid()-1).getTeamName1() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row2$Grp" + row_no + "$SelectMatchType" + cout
							+ "$Team1$txt_TeamName2*GEOM*TEXT SET " + team.get(fixture.get(i).getAwayteamid()-1).getTeamName1() + "\0");
					
					if(fixture.get(i).getMargin() == null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row2$Grp" + row_no + "$SelectMatchType" + cout
								+ "$Team1$txt_Separator*GEOM*TEXT SET " + "VS" + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row2$Grp" + row_no + "$SelectMatchType" + cout
								+ "$Team1$txt_Separator*GEOM*TEXT SET " + fixture.get(i).getMargin() + "\0");
					}
					
					
				}else if(row_no > 4 && row_no <= 6) {
					new_date = fixture.get(i).getDate().split("-")[0];
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row3$DateGrp$txt_Date*GEOM*TEXT SET " + 
							new_date + dateSuffix[Integer.valueOf(new_date)] + " MAY" + "\0");
					
					if(match_name.equalsIgnoreCase(fixture.get(i).getMatchfilename())) {
						omo=1;
						cout="$Highlight";
					}else {
						omo=0;
						cout="$Dehighlight";
					}
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row3$Grp" + row_no + 
							"$SelectMatchType*FUNCTION*Omo*vis_con SET " + omo + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row3$Grp" + row_no + "$SelectMatchType" + cout
							+ "$Team1$txt_TeamName1*GEOM*TEXT SET " + team.get(fixture.get(i).getHometeamid()-1).getTeamName1() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row3$Grp" + row_no + "$SelectMatchType" + cout
							+ "$Team1$txt_TeamName2*GEOM*TEXT SET " + team.get(fixture.get(i).getAwayteamid()-1).getTeamName1() + "\0");
					
					if(fixture.get(i).getMargin() == null) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row3$Grp" + row_no + "$SelectMatchType" + cout
								+ "$Team1$txt_Separator*GEOM*TEXT SET " + "VS" + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$Row3$Grp" + row_no + "$SelectMatchType" + cout
								+ "$Team1$txt_Separator*GEOM*TEXT SET " + fixture.get(i).getMargin() + "\0");
					}
					
				}
				
			}
		}
		
		
		
		print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_sence_path + " C:/Temp/Preview.png In 0.020 FF_In 2.000 Fixtures_In 2.500 \0");
		
	}
	public void populateQulifiers(Socket session_socket,String viz_sence_path,String session_selected_broadcaster,Match match) throws InterruptedException, IOException 
	{		
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0 \0");
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET " + 
				"RESULTS OF THE QUALIFIERS" + "\0");
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$FixturesAll$AllData$FixtureData$BottomInfoGrp$txt_Info*GEOM*TEXT SET " + 
				"ROUNDGLASS PUNJAB FC QUALIFIED DIRECTLY FOR THE GROUP STAGE" + "\0");
		
		print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_sence_path + " C:/Temp/Preview.png In 2.500 FF_In 2.000 Fixtures_In 2.500 \0");
		
	}
	
	public void populateLtPenalty(Socket session_socket,String viz_scene,String valueToProcess,RugbyService rugbyService,Match match,Clock clock, String session_selected_broadcaster) 
			throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			int l=200;
			int iHomeCont = 0, iAwayCont = 0;
			
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$MainScorePart$TeamGrp1$txt_Name*GEOM*TEXT SET " + match.getHomeTeam().getTeamName4() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$MainScorePart$TeamGrp2$txt_Name*GEOM*TEXT SET " + match.getAwayTeam().getTeamName4() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$MainScorePart$TeamGrp1$img_TeamColour*TEXTURE*IMAGE SET " + 
					colors_path + match.getHomeTeamJerseyColor() + RugbyUtil.PNG_EXTENSION + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$MainScorePart$TeamGrp2$img_TeamColour*TEXTURE*IMAGE SET " + 
					colors_path + match.getAwayTeamJerseyColor() + RugbyUtil.PNG_EXTENSION + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$MainScorePart$Seperator$AllScoreGrp$txt_HomeScore*GEOM*TEXT SET " + match.getHomePenaltiesHits() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$MainScorePart$Seperator$AllScoreGrp$txt_AwayScore*GEOM*TEXT SET " + match.getAwayPenaltiesHits() + "\0");
			
			TimeUnit.MILLISECONDS.sleep(l);
			
			for(int p=1;p<=5;p++) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + p + "$SelectPenaltyType$txt_PenaltyNumber*GEOM*TEXT SET " + 
						p + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + p + "$SelectPenaltyType$txt_PenaltyNumber*GEOM*TEXT SET " + 
						p + "\0");
			}
			
			for(String pen : penalties)
			{
				if(pen.toUpperCase().contains(RugbyUtil.HOME + "_" + RugbyUtil.INCREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.HIT)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "1" + "\0");
					
				}else if(pen.toUpperCase().contains(RugbyUtil.HOME + "_" + RugbyUtil.INCREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.MISS)) {
					iHomeCont = iHomeCont + 1;
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "2" + "\0");
					
				}else if(pen.toUpperCase().contains(RugbyUtil.AWAY + "_" + RugbyUtil.INCREMENT + "_" + "PENALTIES$" + "_" + RugbyUtil.HIT)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "1" + "\0");
				}else if(pen.toUpperCase().contains(RugbyUtil.AWAY + "_" + RugbyUtil.INCREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.MISS)) {
					iAwayCont = iAwayCont + 1;
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "2" + "\0");
				}
				
				
				if(pen.toUpperCase().contains(RugbyUtil.HOME + "_" + RugbyUtil.DECREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.HIT)) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0");
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(RugbyUtil.HOME + "_" + RugbyUtil.DECREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.MISS)) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + iHomeCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0");
					if(iHomeCont > 0) {
						iHomeCont = iHomeCont - 1;
					}
				}else if(pen.toUpperCase().contains(RugbyUtil.AWAY + "_" + RugbyUtil.DECREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.HIT)) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0");
					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}else if(pen.toUpperCase().contains(RugbyUtil.AWAY + "_" + RugbyUtil.DECREMENT + "_" + "PENALTIES" + "_" + RugbyUtil.MISS)) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + iAwayCont + 
							"$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0");
					if(iAwayCont > 0) {
						iAwayCont = iAwayCont - 1;
					}
				}
			}
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 1.700 \0");
		}
	}
	public void populateLtPenaltyChange(Socket session_socket,Match match, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			int iHomeCont = 0, iAwayCont = 0;
			int HomeTotal = 0,AwayTotal=0;
			
			iHomeCont = (match.getHomePenaltiesHits() + match.getHomePenaltiesMisses());
			iAwayCont = (match.getAwayPenaltiesHits() + match.getAwayPenaltiesMisses());
			
			HomeTotal = iHomeCont + 5;
			AwayTotal = iAwayCont + 5;
			
			if(((match.getHomePenaltiesHits()+match.getHomePenaltiesMisses())%5) == 0 && ((match.getAwayPenaltiesHits()+match.getAwayPenaltiesMisses())%5) == 0) {
				if(match.getHomePenaltiesHits() == match.getAwayPenaltiesHits()) {
					penalties = new ArrayList<String>();
						for(int p=1;p<=5;p++) {
							print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + p + "$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0");
							print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + p + "$SelectPenaltyType*FUNCTION*Omo*vis_con SET " + "0" + "\0");
						}
				}
			}
			
			for(int h=iHomeCont+1;h<=HomeTotal;h++) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$HomePenalties$" + (h-iHomeCont) + "$SelectPenaltyType$txt_PenaltyNumber*GEOM*TEXT SET " + 
						h + "\0");
			}
			
			for(int a=iAwayCont+1;a<=AwayTotal;a++) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$AllOut$DataGrp$PenalyGrp$PenaltyDots$AwayPenalties$" + (a-iAwayCont) + "$SelectPenaltyType$txt_PenaltyNumber*GEOM*TEXT SET " + 
						a + "\0");
			}
			
		}
	}
	
	public void populateScoreUpdate(Socket session_socket,String viz_scene,RugbyService rugbyService,Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			int l=200;
			String h1="",h2="",h3="",h4="",a1="",a2="",a3="",a4="";
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET " + "2" + "\0");
			TimeUnit.MILLISECONDS.sleep(l);
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase(RugbyUtil.HALF)) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + clock.getMatchHalves().toUpperCase() + " TIME" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(RugbyUtil.FULL)) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + clock.getMatchHalves().toUpperCase() + " TIME" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(RugbyUtil.FIRST)) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + "FIRST HALF" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(RugbyUtil.SECOND)) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + "SECOND HALF" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(RugbyUtil.EXTRA1) || match.getClock().getMatchHalves().equalsIgnoreCase(RugbyUtil.EXTRA2)) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + "EXTRA TIME" + "\0");
			}
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_1$img_LogoBW"
					+ "*TEXTURE*IMAGE SET "+ logo_bw_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_2$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_3$img_LogoOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_4$img_LogoOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_1$img_LogoBW"
					+ "*TEXTURE*IMAGE SET "+ logo_bw_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_2$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_3$img_LogoOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_4$img_LogoOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$ScoreGtp$txt_Score*GEOM*TEXT SET " + match.getHomeTeamScore() + "-" + match.getAwayTeamScore() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$SubHeader$txt_Info*GEOM*TEXT SET " + match.getTournament() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$txt_HomeTeam*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$txt_AwayTeam*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
			
			
			List<String> home_stats = new ArrayList<String>();
			List<String> away_stats = new ArrayList<String>();
			List<Integer> plyr_ids = new ArrayList<Integer>();
			boolean plyr_exist = false;
 			String stats_txt = "",stats_txt_og = "";
 			
			for(int i=0; i<=match.getMatchStats().size()-1; i++) {
				
				if((match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(RugbyUtil.GOAL) 
						|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(RugbyUtil.PENALTY))) {
					
					plyr_exist = false;
					for(Integer plyr_id : plyr_ids) {
						if(match.getMatchStats().get(i).getPlayerId() == plyr_id && 
								(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(RugbyUtil.GOAL) 
										|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(RugbyUtil.OWN_GOAL)
										|| match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(RugbyUtil.PENALTY))) {
							plyr_exist = true;
							break;
						}
					}

					if(plyr_exist == false) {
						plyr_ids.add(match.getMatchStats().get(i).getPlayerId());
						stats_txt = rugbyService.getPlayer(RugbyUtil.PLAYER, 
							String.valueOf(match.getMatchStats().get(i).getPlayerId())).getTicker_name().toUpperCase()+ " " + 
							RugbyFunctions.calExtraTimeGoal(match.getMatchStats().get(i).getMatchHalves(), match.getMatchStats().get(i).getTotalMatchSeconds()) + 
							RugbyFunctions.goal_shortname(match.getMatchStats().get(i).getStats_type());
						
						for(int j=i+1; j<=match.getMatchStats().size()-1; j++) {
							if (match.getMatchStats().get(i).getPlayerId() == match.getMatchStats().get(j).getPlayerId()
								&& (match.getMatchStats().get(j).getStats_type().equalsIgnoreCase(RugbyUtil.GOAL)
								|| match.getMatchStats().get(j).getStats_type().equalsIgnoreCase(RugbyUtil.PENALTY))) {
								
								stats_txt = stats_txt.trim() + "," + 
								RugbyFunctions.calExtraTimeGoal(match.getMatchStats().get(j).getMatchHalves(), match.getMatchStats().get(j).getTotalMatchSeconds()) 
										+ RugbyFunctions.goal_shortname(match.getMatchStats().get(j).getStats_type());
							}
						}
						switch (RugbyFunctions.getPlayerSquadType(match.getMatchStats().get(i).getPlayerId(),match.getMatchStats().get(i).getStats_type() ,match)) {
						case RugbyUtil.HOME:
							home_stats.add(stats_txt);
							break;
						case RugbyUtil.AWAY:
							away_stats.add(stats_txt);
							break;
						}
					}
				}else if(match.getMatchStats().get(i).getStats_type().equalsIgnoreCase(RugbyUtil.OWN_GOAL)) {
					stats_txt_og = rugbyService.getPlayer(RugbyUtil.PLAYER, 
							String.valueOf(match.getMatchStats().get(i).getPlayerId())).getTicker_name().toUpperCase()+ " " + 
							RugbyFunctions.calExtraTimeGoal(match.getMatchStats().get(i).getMatchHalves(),match.getMatchStats().get(i).getTotalMatchSeconds()) + 
								RugbyFunctions.goal_shortname(match.getMatchStats().get(i).getStats_type());
						
						switch (RugbyFunctions.getPlayerSquadType(match.getMatchStats().get(i).getPlayerId(),match.getMatchStats().get(i).getStats_type() ,match)) {
						case RugbyUtil.HOME:
							home_stats.add(stats_txt_og);
							break;
						case RugbyUtil.AWAY:
							away_stats.add(stats_txt_og);
							break;
						}
				}
			}
			
			if(match.getHomeTeamScore() == 0 && match.getAwayTeamScore()==0) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0");
			}else {
				
				//System.out.println("Home:" + home_stats.size() + " Away : " + away_stats.size());
				
				if (home_stats.size() == 0) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0");
				}else if(home_stats.size() <= 2) { 
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "1" + "\0");
				}else if(home_stats.size() <= 4) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "2" + "\0");
				}else if(home_stats.size() <= 6){
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "3" + "\0");
				}else if(home_stats.size() <= 8){
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "4" + "\0");
				}
				
				if (away_stats.size() == 0) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0");
				}else if(away_stats.size() <= 2) { 
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "1" + "\0");
				}else if(away_stats.size() <= 4) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "2" + "\0");
				}else if(away_stats.size() <= 6){
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "3" + "\0");
				}else if(away_stats.size() <= 8){
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "4" + "\0");
				}
				
			}
			
			for(int i=0;i<=home_stats.size()-1;i++) {
				if(i < 2) { 
					h1 = h1 + home_stats.get(i); 
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$First$txt_Scorer1*GEOM*TEXT SET " + h1 + "\0");
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 4) {
					h2 = h2 + home_stats.get(i);
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$Second$txt_Scorer2*GEOM*TEXT SET " + h2 + "\0");
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 6){
					h3 = h3 + home_stats.get(i);
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$Third$txt_Scorer3*GEOM*TEXT SET " + h3 + "\0");
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 8){
					h4 = h4 + home_stats.get(i);
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select$Fourth$txt_Scorer4*GEOM*TEXT SET " + h4 + "\0");
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
			
			for(int i=0;i<=away_stats.size()-1;i++) {
				if(i < 2) { 
					a1 = a1 + away_stats.get(i); 
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$First$txt_Scorer1*GEOM*TEXT SET " + a1 + "\0");
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 4) {
					a2 = a2 + away_stats.get(i);
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$Second$txt_Scorer2*GEOM*TEXT SET " + a2 + "\0");
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 6){
					a3 = a3 + away_stats.get(i);
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$Third$txt_Scorer3*GEOM*TEXT SET " + a3 + "\0");
					TimeUnit.MILLISECONDS.sleep(l);
				}else if(i < 8){
					a4 = a4 + away_stats.get(i);
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select$Fourth$txt_Scorer4*GEOM*TEXT SET " + a4 + "\0");
					TimeUnit.MILLISECONDS.sleep(l);
				}
			}
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.340 \0");
		}
	}
	public void populateLtMatchId(Socket session_socket,String viz_scene,RugbyService rugbyService,Match match,Clock clock, String session_selected_broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET " + "2" + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_1$img_LogoBW"
					+ "*TEXTURE*IMAGE SET "+ logo_bw_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_2$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_3$img_LogoOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_4$img_LogoOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_1$img_LogoBW"
					+ "*TEXTURE*IMAGE SET "+ logo_bw_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_2$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_3$img_LogoOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_4$img_LogoOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase(RugbyUtil.HALF)) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + clock.getMatchHalves().toUpperCase() + " TIME" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(RugbyUtil.FULL)) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + clock.getMatchHalves().toUpperCase() + " TIME" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(RugbyUtil.FIRST)) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + "FIRST HALF" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(RugbyUtil.SECOND)) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + "SECOND HALF" + "\0");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase(RugbyUtil.EXTRA1) || match.getClock().getMatchHalves().equalsIgnoreCase(RugbyUtil.EXTRA2)) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + "EXTRA TIME" + "\0");
			}
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$ScoreGtp$txt_Score*GEOM*TEXT SET " + match.getHomeTeamScore() + "-" + match.getAwayTeamScore() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$txt_HomeTeam*GEOM*TEXT SET " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$txt_AwayTeam*GEOM*TEXT SET " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0");
			
			String text_to_return = "";
			try (BufferedReader br = new BufferedReader(new FileReader(RugbyUtil.RUGBY_DIRECTORY + "PenaltyResult.txt"))) {
				while((text_to_return = br.readLine()) != null) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$SubHeader$txt_Info*GEOM*TEXT SET " + text_to_return + "\0");
				}
			}
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.340 \0");
		}
	}
	public void populateLTMatchPromoSingle(Socket session_socket,String viz_scene, int match_number ,List<Team> team,List<Fixture> fix,List<Ground>ground,Match match, String broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET " + "2" + "\0");
			
			
			for(Team TM : team) {
				if(fix.get(match_number - 1).getHometeamid() == TM.getTeamId()) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_1$img_LogoBW"
							+ "*TEXTURE*IMAGE SET "+ logo_bw_path + TM.getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_2$img_Logo"
							+ "*TEXTURE*IMAGE SET "+ logo_path + TM.getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_3$img_LogoOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + TM.getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_4$img_LogoOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + TM.getTeamName4().toLowerCase() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$txt_HomeTeam*GEOM*TEXT SET " + TM.getTeamName1().toUpperCase() + "\0");
				}
				if(fix.get(match_number - 1).getAwayteamid() == TM.getTeamId()) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_1$img_LogoBW"
							+ "*TEXTURE*IMAGE SET "+ logo_bw_path + TM.getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_2$img_Logo"
							+ "*TEXTURE*IMAGE SET "+ logo_path + TM.getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_3$img_LogoOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + TM.getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_4$img_LogoOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + TM.getTeamName4().toLowerCase() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$txt_AwayTeam*GEOM*TEXT SET " + TM.getTeamName1().toUpperCase() + "\0");
				}
			}
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + fix.get(match_number - 1).getGroupName() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$ScoreGtp$txt_Score*GEOM*TEXT SET " + "VS" + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0");
			
			String Date = "";
			Calendar cal = Calendar.getInstance();
			Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
			if(fix.get(match_number-1).getDate().equalsIgnoreCase(Date)) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$SubHeader$txt_Info*GEOM*TEXT SET " + "COMING UP AT " + fix.get(match_number-1).getTime() 
						+ " LOCAL TIME" + "\0");
			}else {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$SubHeader$txt_Info*GEOM*TEXT SET " + fix.get(match_number-1).getDate() + 
						" AT " + fix.get(match_number-1).getTime() + " LOCAL TIME" + "\0");
			}
			
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.340 \0");
		}
	}
	public ScoreBug populateScoreBugPromo(boolean is_this_updating,ScoreBug scorebug,Socket session_socket, int match_number ,List<Team> team,List<Fixture> fix,List<Ground>ground,Match match, String broadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			String team_name = "",newDate = "";
			List<String> data_name = new ArrayList<String>();
			
			String[] dateSuffix = {
					"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
					
					"th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
					
					"th", "st", "nd", "rd", "th", "th", "th", "th", "th","th",
					
					"th", "st"
			};
			
			for(Team TM : team) {
				if(fix.get(match_number - 1).getHometeamid() == TM.getTeamId()) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$TeamsGrpAll$TeamAll$Line1$PromoAll$Promo1$lg_Badge1"
							+ "*TEXTURE*IMAGE SET "+ logo_path + TM.getTeamName4().toLowerCase() + "\0");
					TimeUnit.MILLISECONDS.sleep(200);
					team_name = TM.getTeamName4().toUpperCase();
					data_name.add(team_name);
					if(is_this_updating == false) {
						scorebug.setScorebug_name(team_name);
						scorebug.setLast_scorebug_name(team_name);
//						is_this_updating = true;
					}
				}
			}
			
			for(Team TM : team) {
				if(fix.get(match_number - 1).getAwayteamid() == TM.getTeamId()) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$TeamsGrpAll$TeamAll$Line1$PromoAll$Promo1$lg_Badge2"
							+ "*TEXTURE*IMAGE SET "+ logo_path + TM.getTeamName4().toLowerCase() + "\0");
					
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$TeamsGrpAll$TeamAll$Line1$PromoAll$Promo1$Match$txt_Match"
							+ "*GEOM*TEXT SET " + team_name + " vs " + TM.getTeamName4().toUpperCase() + "\0");
					TimeUnit.MILLISECONDS.sleep(200);
//					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$txt_AwayTeam*GEOM*TEXT SET " + TM.getTeamName1().toUpperCase() + "\0");
				}
				
				
			}
			
//			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$TeamsGrpAll$TeamAll$Line1$PromoAll$Promo1$txt_Group"
//					+ "*GEOM*TEXT SET " + "" + "\0");
			print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$HeadTimeGrp$txt_Head_Time"
					+ "*GEOM*TEXT SET " + "HERO CLUB PLAYOFF" + "\0");
			
			String Date = "";
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, +1);
			Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
			if(fix.get(match_number-1).getDate().equalsIgnoreCase(Date)) {
				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$TeamsGrpAll$TeamAll$Line1$PromoAll$Promo1$txt_Group"
						+ "*GEOM*TEXT SET " + "TOMORROW " + fix.get(match_number-1).getTime() + "\0");
//				print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$HeadTimeGrp$txt_Head_Time"
//						+ "*GEOM*TEXT SET " + "TOMORROW " + fix.get(match_number-1).getTime() + "\0");
			}else {
				cal.add(Calendar.DATE, -1);
				Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
				if(fix.get(match_number-1).getDate().equalsIgnoreCase(Date)) {
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$TeamsGrpAll$TeamAll$Line1$PromoAll$Promo1$txt_Group"
							+ "*GEOM*TEXT SET " + "COMING UP" + " AT " + fix.get(match_number-1).getTime() + "\0");
//					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$HeadTimeGrp$txt_Head_Time"
//							+ "*GEOM*TEXT SET " + "COMING UP" + " AT " + fix.get(match_number-1).getTime() + "\0");
				}else {
					newDate = fix.get(match_number-1).getDate().split("-")[0];
					if(Integer.valueOf(newDate) < 10) {
						newDate = newDate.replaceFirst("0", "");
					}
					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$TeamsGrpAll$TeamAll$Line1$PromoAll$Promo1$txt_Group"
							+ "*GEOM*TEXT SET " + newDate + dateSuffix[Integer.valueOf(newDate)] + " MAY" + " AT " + fix.get(match_number-1).getTime() + "\0");
//					print_writer.println("-1 RENDERER*FRONT_LAYER*TREE*$Main$All$AllOut$DataGrp$PromoAll$Grp1$PromoDataAll$HeadTimeGrp$txt_Head_Time"
//							+ "*GEOM*TEXT SET " + newDate + dateSuffix[Integer.valueOf(newDate)] + " MAY" + " AT " + fix.get(match_number-1).getTime() + "\0");
				}
				
			}
			
			data_name.clear();
//			print_writer.println("-1 RENDERER PREVIEW SCENE*/Default/LT C:/Temp/Preview.png In 2.340 \0");
		}
		
		scorebug.setLast_scorebug_promo(scorebug.getScorebug_promo());
		return scorebug;
	}
	public void populateStaff(Socket session_socket,String viz_scene, Staff st,List<Team> team ,Match match, String selectedbroadcaster) throws InterruptedException, IOException{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 0 \0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$Ani_1$img_LogoBW"
					+ "*TEXTURE*IMAGE SET "+ logo_bw_path + team.get(st.getClubId() - 1).getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$Ani_2$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(st.getClubId() - 1).getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$Ani_3$img_LogoOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + team.get(st.getClubId() - 1).getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$Ani_4$img_LogoOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + team.get(st.getClubId() - 1).getTeamName4().toLowerCase() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$Header$Dataall$txt_Name*GEOM*TEXT SET " + st.getName().toUpperCase() + "\0");
				
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
					st.getRole().toUpperCase() + ", " + team.get(st.getClubId() - 1).getTeamName1().toUpperCase() + "\0");
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.340 \0");
		}
	}
	public void populateNameSuper(Socket session_socket,String viz_scene, NameSuper ns ,Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			int l = 4;
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 0 \0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$Logo_Grp$Nquad$img_Badges"
					+ "*TEXTURE*IMAGE SET "+ logo2_path + "HeroTrination" + "\0");
			
			if(ns.getSponsor() == null) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$Ani_1$img_LogoBW"
						+ "*TEXTURE*IMAGE SET "+ logo_bw_path + "TLogo" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$Ani_2$img_Logo"
						+ "*TEXTURE*IMAGE SET "+ logo_path + "TLogo" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$Ani_3$img_LogoOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + "TLogo" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$Ani_4$img_LogoOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + "TLogo" + "\0");
			}else {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$Ani_1$img_LogoBW"
						+ "*TEXTURE*IMAGE SET "+ logo_bw_path + ns.getSponsor() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$Ani_2$img_Logo"
						+ "*TEXTURE*IMAGE SET "+ logo_path + ns.getSponsor() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$Ani_3$img_LogoOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + ns.getSponsor() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$LogoGrp$Ani_4$img_LogoOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + ns.getSponsor() + "\0");
			}
			
			if(ns.getFirstname() == null) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
						ns.getSurname() + "\0");
				
			}else if(ns.getSurname() == null) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
						ns.getFirstname() + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
			}else {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
						ns.getFirstname() + " " + ns.getSurname() + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
			}
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$GenericNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
					ns.getSubLine().toUpperCase() + "\0");
			TimeUnit.MILLISECONDS.sleep(l);
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.340 \0");
		}
		
	}
	public void populateNameSuperPlayer(Socket session_socket,String viz_scene, int TeamId, String captainGoalKeeper, int playerId, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			String Home_or_Away="";
			int l = 4;
			
			if(captainGoalKeeper.equalsIgnoreCase("PLAYER OF THE MATCH")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 8 \0");
				
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Hero$BottomGrp$InfoGrp$InfoDataGrp$txt_BottomInfo*GEOM*TEXT SET " + 
						" OF THE MATCH " + "\0");
				
				if(TeamId == match.getHomeTeamId()) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Hero$LogoGrp$Ani_1$img_LogoBW"
							+ "*TEXTURE*IMAGE SET "+ logo_bw_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Hero$LogoGrp$Ani_2$img_Logo"
							+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Hero$LogoGrp$Ani_3$img_LogoOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Hero$LogoGrp$Ani_4$img_LogoOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
					TimeUnit.MILLISECONDS.sleep(l);
					
					Home_or_Away = match.getHomeTeam().getTeamName1().toUpperCase();
					for(Player hs : match.getHomeSquad()) {
						if(playerId == hs.getPlayerId()) {
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Hero$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
									hs.getFull_name().toUpperCase() + "\0");
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Hero$Header$Dataall$txt_Number*GEOM*TEXT SET " + 
									hs.getJersey_number() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							
						}
					}
					for(Player hsub : match.getHomeSubstitutes()) {
						if(playerId == hsub.getPlayerId()) {
							
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Hero$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
									hsub.getFull_name().toUpperCase() + "\0");
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Hero$Header$Dataall$txt_Number*GEOM*TEXT SET " + 
									hsub.getJersey_number() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							
						}
					}
				}
				else {
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Hero$LogoGrp$Ani_1$img_LogoBW"
							+ "*TEXTURE*IMAGE SET "+ logo_bw_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Hero$LogoGrp$Ani_2$img_Logo"
							+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Hero$LogoGrp$Ani_3$img_LogoOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Hero$LogoGrp$Ani_4$img_LogoOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
					TimeUnit.MILLISECONDS.sleep(l);
					
					Home_or_Away = match.getAwayTeam().getTeamName1().toUpperCase();
					for(Player as : match.getAwaySquad()) {
						if(playerId == as.getPlayerId()) {
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Hero$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
									as.getFull_name().toUpperCase() + "\0");
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Hero$Header$Dataall$txt_Number*GEOM*TEXT SET " + 
									as.getJersey_number() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							
						}
					}
					for(Player asub : match.getAwaySubstitutes()) {
						if(playerId == asub.getPlayerId()) {
							
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Hero$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
									asub.getFull_name().toUpperCase() + "\0");
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Hero$Header$Dataall$txt_Number*GEOM*TEXT SET " + 
									asub.getJersey_number() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							
						}
					}
				}
				
				
			}else {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 1 \0");
				
				if(TeamId == match.getHomeTeamId()) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$LogoGrp$Ani_1$img_LogoBW"
							+ "*TEXTURE*IMAGE SET "+ logo_bw_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$LogoGrp$Ani_2$img_Logo"
							+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$LogoGrp$Ani_3$img_LogoOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$LogoGrp$Ani_4$img_LogoOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
					TimeUnit.MILLISECONDS.sleep(l);
					
					Home_or_Away = match.getHomeTeam().getTeamName1().toUpperCase();
					for(Player hs : match.getHomeSquad()) {
						if(playerId == hs.getPlayerId()) {
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$Header$Dataall$txt_Number*GEOM*TEXT SET " + 
									hs.getJersey_number() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
									hs.getFull_name().toUpperCase() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							
							if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
								print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
										match.getHomeTeam().getTeamName1().toUpperCase() + "\0");
								TimeUnit.MILLISECONDS.sleep(l);
							}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
								print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
										hs.getRole().toUpperCase() + " , " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0");
							}
						}
					}
					for(Player hsub : match.getHomeSubstitutes()) {
						if(playerId == hsub.getPlayerId()) {
							
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$Header$Dataall$txt_Number*GEOM*TEXT SET " + 
									hsub.getJersey_number() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
									hsub.getFull_name().toUpperCase() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							
							if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
								print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
										match.getHomeTeam().getTeamName1().toUpperCase() + "\0");
							}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
								print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
										hsub.getRole().toUpperCase() + " , " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0");
							}
						}
					}
				}
				else {
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$LogoGrp$Ani_1$img_LogoBW"
							+ "*TEXTURE*IMAGE SET "+ logo_bw_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$LogoGrp$Ani_2$img_Logo"
							+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$LogoGrp$Ani_3$img_LogoOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$LogoGrp$Ani_4$img_LogoOutline"
							+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
					TimeUnit.MILLISECONDS.sleep(l);
					
					Home_or_Away = match.getAwayTeam().getTeamName1().toUpperCase();
					for(Player as : match.getAwaySquad()) {
						if(playerId == as.getPlayerId()) {
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$Header$Dataall$txt_Number*GEOM*TEXT SET " + 
									as.getJersey_number() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
									as.getFull_name().toUpperCase() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							
							if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
								print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
										match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
							}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
								print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
										as.getRole().toUpperCase() + " , " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
							}
						}
					}
					for(Player asub : match.getAwaySubstitutes()) {
						if(playerId == asub.getPlayerId()) {
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$Header$Dataall$txt_Number*GEOM*TEXT SET " + 
									asub.getJersey_number() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
									asub.getFull_name().toUpperCase() + "\0");
							TimeUnit.MILLISECONDS.sleep(l);
							
							if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("PLAYER")) {
								print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
										match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
							}else if(captainGoalKeeper.toUpperCase().equalsIgnoreCase("Player_Role")) {
								print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
										asub.getRole().toUpperCase() + " , " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
							}
						}
					}
				}
				
				switch(captainGoalKeeper.toUpperCase())
				{
				case "CAPTAIN":
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
							captainGoalKeeper.toUpperCase() + " , " + Home_or_Away + "\0");
					break;
				/*case "PLAYER OF THE MATCH":
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
							"PLAYER OF THE MATCH " + "\0");
					break;*/
				case "GOAL_KEEPER":
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
							"GOAL-KEEPER" + " , " + Home_or_Away + "\0");
					break;
				case "PLAYER_TODAY_GOAL":
					int player_goal_count=0;
					for(MatchStats ms : match.getMatchStats()) {
						if(ms.getStats_type().equalsIgnoreCase("goal")) {
							if(ms.getPlayerId() == playerId) {
								player_goal_count = player_goal_count + 1;
							}
						}else if(ms.getStats_type().equalsIgnoreCase("penalty")) {
							if(ms.getPlayerId() == playerId) {
								player_goal_count = player_goal_count + 1;
							}
						}
					}
					if(player_goal_count != 0) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
								"GOALS TODAY - " + player_goal_count + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
								Home_or_Away + "\0");
					}
					
					break;
				case "GOAL_SCORER":
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
							"GOAL SCORER" + " , " + Home_or_Away + "\0");
					break;
				case "CAPTAIN-GOALKEEPER":
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$PlayerNameSuper$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
							"CAPTAIN & GOAL-KEEPER" + " , " + Home_or_Away + "\0");
					break;
				}
			}
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.340 \0");	
		}
	}
	public void populateNameSuperCard(Socket session_socket,String viz_scene, int TeamId, String cardType, int playerId, Match match, String selectedbroadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		} else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			
			int l = 4;
			print_writer.println("-1 RENDERER*TREE*$Main$Select*FUNCTION*Omo*vis_con SET 3 \0");
			
			if(TeamId == match.getHomeTeamId()) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$LogoGrp$Ani_1$img_LogoBW"
						+ "*TEXTURE*IMAGE SET "+ logo_bw_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$LogoGrp$Ani_2$img_Logo"
						+ "*TEXTURE*IMAGE SET "+ logo_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$LogoGrp$Ani_3$img_LogoOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$LogoGrp$Ani_4$img_LogoOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getHomeTeam().getTeamName4().toLowerCase() + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				
				for(Player hs : match.getHomeSquad()) {
					if(playerId == hs.getPlayerId()) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Header$Dataall$txt_Number*GEOM*TEXT SET " + hs.getJersey_number() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Header$Dataall$txt_Name*GEOM*TEXT SET " + hs.getFull_name().toUpperCase() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
								hs.getRole().toUpperCase() + " , " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0");
						
					}
				}
				for(Player hsub : match.getHomeSubstitutes()) {
					if(playerId == hsub.getPlayerId()) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Header$Dataall$txt_Number*GEOM*TEXT SET " + hsub.getJersey_number() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Header$Dataall$txt_Name*GEOM*TEXT SET " + hsub.getFull_name().toUpperCase() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
								hsub.getRole().toUpperCase() + " , " + match.getHomeTeam().getTeamName1().toUpperCase() + "\0");
					}
				}
			}
			else {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$LogoGrp$Ani_1$img_LogoBW"
						+ "*TEXTURE*IMAGE SET "+ logo_bw_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$LogoGrp$Ani_2$img_Logo"
						+ "*TEXTURE*IMAGE SET "+ logo_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$LogoGrp$Ani_3$img_LogoOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$LogoGrp$Ani_4$img_LogoOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + match.getAwayTeam().getTeamName4().toLowerCase() + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				
				for(Player as : match.getAwaySquad()) {
					if(playerId == as.getPlayerId()) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Header$Dataall$txt_Number*GEOM*TEXT SET " + as.getJersey_number() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Header$Dataall$txt_Name*GEOM*TEXT SET " + as.getFull_name().toUpperCase() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
								as.getRole().toUpperCase() + " , " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
					}
				}
				for(Player asub : match.getAwaySubstitutes()) {
					if(playerId == asub.getPlayerId()) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Header$Dataall$txt_Number*GEOM*TEXT SET " + asub.getJersey_number() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Header$Dataall$txt_Name*GEOM*TEXT SET " + asub.getFull_name().toUpperCase() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$BottomGrp$InfoGrp$txt_BottomInfo*GEOM*TEXT SET " + 
								asub.getRole().toUpperCase() + " , " + match.getAwayTeam().getTeamName1().toUpperCase() + "\0");
					}
				}
			}
			
			switch(cardType.toUpperCase())
			{
			case RugbyUtil.YELLOW:
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Header$Dataall$Card$Select*FUNCTION*Omo*vis_con SET 0 \0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case RugbyUtil.RED:
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Header$Dataall$Card$Select*FUNCTION*Omo*vis_con SET 1 \0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			case "YELLOW_RED":
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$NameSuperCard$Header$Dataall$Card$Select*FUNCTION*Omo*vis_con SET 2 \0");
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			}

			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.340 \0");		
		}
	}
	public void populateSubstitute(Socket session_socket,String viz_scene,int Team_id,String Num_Of_Subs,List<Player> plyr,List<Team> team, Match match, String session_selected_broadcaster) throws InterruptedException, IOException
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			int l = 200;
			List<Event> evnt = new ArrayList<Event>();
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 4 \0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$LogoGrp$Ani_1$img_LogoBW"
					+ "*TEXTURE*IMAGE SET "+ logo_bw_path + team.get(Team_id - 1).getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$LogoGrp$Ani_2$img_Logo"
					+ "*TEXTURE*IMAGE SET "+ logo_path + team.get(Team_id - 1).getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$LogoGrp$Ani_3$img_LogoOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + team.get(Team_id - 1).getTeamName4().toLowerCase() + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$LogoGrp$Ani_4$img_LogoOutline"
					+ "*TEXTURE*IMAGE SET "+ logo_outline_path + team.get(Team_id - 1).getTeamName4().toLowerCase() + "\0");
			
			for(int i = 0; i<=match.getEvents().size()-1; i++) { 
				if(match.getEvents().get(i).getEventType().equalsIgnoreCase("replace")) {
					if(Team_id ==plyr.get(match.getEvents().get(i).getOnPlayerId()-1).getTeamId()) {
						if(match.getHomeTeamId() == plyr.get(match.getEvents().get(i).getOnPlayerId()-1).getTeamId()) {
							evnt.add(match.getEvents().get(i)); 
						}else if(match.getAwayTeamId() == plyr.get(match.getEvents().get(i).getOnPlayerId()-1).getTeamId()) {
							evnt.add(match.getEvents().get(i)); 
						} 
					} 
				}
			}
			
			switch(Num_Of_Subs.toUpperCase())
			{
			case "SINGLE":
				
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$Header$Dataall$InPlayer$txt_Number*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$Header$Dataall$InPlayer$txt_Name*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getFull_name().toUpperCase() + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$Header$Dataall$OutPlayer$txt_Number*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$Header$Dataall$OutPlayer$txt_Name*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getFull_name().toUpperCase() + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				
				
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$BottomGrp$InfoGrp$OutPlayer$txt_Number*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$BottomGrp$InfoGrp$OutPlayer$txt_Name*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getFull_name().toUpperCase() + "\0");
				
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$BottomGrp$InfoGrp$InPlayer$txt_Number*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() + "\0");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$LT_Subs$BottomGrp$InfoGrp$InPlayer$txt_Name*GEOM*TEXT SET " + 
						plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getFull_name().toUpperCase() + "\0");
				
				
				TimeUnit.MILLISECONDS.sleep(l);
				break;
			/*case "DOUBLE":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfIn 1;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfOut 1;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2A " + plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2A " + plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2A " + logo_path + "Red_Arrow" + RugbyUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1A " + plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1A " + plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1A " + logo_path + "Green_Arrow" + RugbyUtil.PNG_EXTENSION + ";");
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2B " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2B " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2B " + logo_path + "Red_Arrow" + RugbyUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1B " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1B " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1B " + logo_path + "Green_Arrow" + RugbyUtil.PNG_EXTENSION + ";");
				
				break;
				
			case "TRIPLE":
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfIn 2;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vNumberOfOut 2;");
				TimeUnit.MILLISECONDS.sleep(l);
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2A " + plyr.get(evnt.get(evnt.size() - 3).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2A " + plyr.get(evnt.get(evnt.size() - 3).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2A " + logo_path + "Red_Arrow" + RugbyUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1A " + plyr.get(evnt.get(evnt.size() - 3).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1A " + plyr.get(evnt.get(evnt.size() - 3).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1A " + logo_path + "Green_Arrow" + RugbyUtil.PNG_EXTENSION + ";");
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2B " + plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2B " + plyr.get(evnt.get(evnt.size() - 2).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2B " + logo_path + "Red_Arrow" + RugbyUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1B " + plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1B " + plyr.get(evnt.get(evnt.size() - 2).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1B " + logo_path + "Green_Arrow" + RugbyUtil.PNG_EXTENSION + ";");
				
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber2C " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName2C " + plyr.get(evnt.get(evnt.size() - 1).getOffPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow2C " + logo_path + "Red_Arrow" + RugbyUtil.PNG_EXTENSION + ";");
			
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerNumber1C " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getJersey_number() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tPlayerName1C " + plyr.get(evnt.get(evnt.size() - 1).getOnPlayerId() - 1).getTicker_name().toUpperCase() +";");
				print_writer.println("LAYER2*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgArrow1C " + logo_path + "Green_Arrow" + RugbyUtil.PNG_EXTENSION + ";");
				
				break;*/
			}
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.340 \0");	
		}
	}
	public void populateOfficials(Socket session_socket,String viz_scene,List<Officials> officials,Match match, String session_selected_broadcaster) throws InterruptedException, IOException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 5 \0");

			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$LT_Officials$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
					officials.get(0).getReferee()  + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$LT_Officials$BottomGrp$InfoGrp$txt_Name*GEOM*TEXT SET " + 
					officials.get(0).getAssistantReferee1() + " & " + officials.get(0).getAssistantReferee2() + "\0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$LT_Officials$BottomGrp$Dataall2$txt_Name*GEOM*TEXT SET " + 
					officials.get(0).getFourthOfficial() + "\0");
			
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.340 \0");	
		}
	}
	public void populateHeatMapPeakDistance(Socket session_socket,String viz_scene,int TeamId,String Value,int Playerid,List<Player> plyr,Match match, String session_selected_broadcaster) throws InterruptedException, IOException, SAXException, ParserConfigurationException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			int team_number=0;
			String team_name="";
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 7 \0");
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeatMap$HeatMap$Header$Dataall$txt_Name$txt_Number*GEOM*TEXT SET " + 
					plyr.get(Playerid-1).getJersey_number()  + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeatMap$HeatMap$Header$Dataall$txt_Name$txt_PlayerName*GEOM*TEXT SET " + 
					plyr.get(Playerid-1).getTicker_name()  + "\0");
			
			if(match.getHomeTeamId() == TeamId) {
				team_number = 0;
				team_name = match.getHomeTeam().getTeamName4();
			}else if(match.getAwayTeamId() == TeamId){
				team_number = 1;
				team_name = match.getAwayTeam().getTeamName4();
			}
			
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeatMap$HeatMap$Header$Dataall$txt_Name$txt_TeamName*GEOM*TEXT SET " + 
					team_name  + "\0");
			
//			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeatMapAll$HeatMapGrp$Logo_Grp$Nquad$img_Badges" + "*TEXTURE*IMAGE SET "+ photos_path + 
//					team_name + "\\" + plyr.get(Playerid-1).getPhoto() + RugbyUtil.PNG_EXTENSION + "\0");
			
//			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeatMapAll$HeatMapGrp$BandAll$NameDataGrp$Field$Football_Pitch*ACTIVE SET 0 \0");
			
			switch(Value.toUpperCase()) {
			case "HEATMAP":
				TimeUnit.SECONDS.sleep(2);
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeatMap$HeatMap$DataAll$DataOut$Ground$img_HeatMap*TEXTURE*IMAGE SET "+ image_path + 
						"playerheatmap" + team_number + "_" + plyr.get(Playerid-1).getJersey_number() + ".jpg" + "\0");
				break;
			case "PEAKDISTANCE":
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$HeatMap$HeatMap$DataAll$DataOut$Ground$img_HeatMap*TEXTURE*IMAGE SET "+ image_path + 
						"playerpeakdistancegraph" + team_number + "_" + plyr.get(Playerid-1).getJersey_number() + ".jpg" + "\0");
				break;
			}
			  
			print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.340 \0");	
		}
	}
	public void populateTopStats(Socket session_socket,String viz_scene,String Value,List<TeamStats>teamStats,List<Player> player,List<Team> team,Match match, String session_selected_broadcaster) throws InterruptedException, IOException, SAXException, ParserConfigurationException 
	{
		if (match == null) {
			this.status = "ERROR: Match is null";
		}else {
			
			String team_name="";
			int row = 0;
			
			List<PlayerStats> top_stats = new ArrayList<PlayerStats>();
			
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET 6 \0");
			
			if(Value.toUpperCase().equalsIgnoreCase("TEAM TOP SPEED")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Top5All$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
						"TOP SPEED" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Top5All$Header$Dataall$txt_Sub*GEOM*TEXT SET " + 
						"THIS MATCH" + " (KPH)"  + "\0");
			}else if(Value.toUpperCase().equalsIgnoreCase("HIGHEST DISTANCE")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Top5All$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
						"HIGHEST DISTANCE" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Top5All$Header$Dataall$txt_Sub*GEOM*TEXT SET " + 
						"THIS MATCH"  + " (KM)" + "\0");
			}else if(Value.toUpperCase().equalsIgnoreCase("BEST RUNNER")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Top5All$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
						"TOP RUNNER" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Top5All$Header$Dataall$txt_Sub*GEOM*TEXT SET " + 
						"THIS MATCH"  + " (KPH)" + "\0");
			}else if(Value.toUpperCase().equalsIgnoreCase("BEST SPRINTER")) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Top5All$Header$Dataall$txt_Name*GEOM*TEXT SET " + 
						"TOP SPRINT" + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Top5All$Header$Dataall$txt_Sub*GEOM*TEXT SET " + 
						"THIS MATCH"  + " (KPH)" + "\0");
			}
			
			
	        for(int i=0;i<= teamStats.size()-1;i++) {
        		for(int j=0;j<= teamStats.get(i).getTopStats().size()-1;j++) {
        			if(teamStats.get(i).getTopStats().get(j).getHeader().equalsIgnoreCase(Value)) {
        				for(PlayerStats ps : teamStats.get(i).getTopStats().get(j).getPlayersStats()) {
        					top_stats.add(ps);
        				}
		        	}
        		}
	        }
	        
	        Collections.sort(top_stats,new RugbyFunctions.PlayerStatsComparator());
	        
	        print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Top5All$DataAll$DataOut$ScorrerTeam1"
					+ "*FUNCTION*Grid*num_row SET 5 \0");
	        
	        for(int m=0; m<= top_stats.size() - 1; m++) {
	        	row = row + 1;
	        	if(row <= 5) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Top5All$DataAll$DataOut$ScorrerTeam1$Row" + row + ""
							+ "$Out$In$NumberGrp$txt_Number*GEOM*TEXT SET " + top_stats.get(m).getJerseyNumber()  + "\0");
					
					for(Team tm : team) {
						if(top_stats.get(m).getTeam_name().equalsIgnoreCase(tm.getTeamName1())) {
							team_name = tm.getTeamName4();
						}
					}
					print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Top5All$DataAll$DataOut$ScorrerTeam1$Row" + row + ""
							+ "$Out$In$txt_Name*GEOM*TEXT SET " + top_stats.get(m).getFirst_name().toUpperCase() + " - " + team_name  + "\0");
					
					if(Value.toUpperCase().equalsIgnoreCase("HIGHEST DISTANCE")) {
						DecimalFormat df_bo = new DecimalFormat("0.0");
						double df = Double.valueOf(top_stats.get(m).getValue())/1000;
						df_bo.setRoundingMode(RoundingMode.UP); 
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Top5All$DataAll$DataOut$ScorrerTeam1$Row" + row + ""
								+ "$Out$In$StatValueGrp$txt_Value*GEOM*TEXT SET " + df_bo.format(df) + "\0");
					}else if(Value.toUpperCase().equalsIgnoreCase("TEAM TOP SPEED")) {
						DecimalFormat df_ts = new DecimalFormat("0.0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Top5All$DataAll$DataOut$ScorrerTeam1$Row" + row + ""
								+ "$Out$In$StatValueGrp$txt_Value*GEOM*TEXT SET " + df_ts.format(Double.valueOf(top_stats.get(m).getValue())) + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$Select$Top5All$DataAll$DataOut$ScorrerTeam1$Row" + row + ""
								+ "$Out$In$StatValueGrp$txt_Value*GEOM*TEXT SET " + top_stats.get(m).getValue()  + "\0");
					}
	        	}
			}
	        print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 2.340 \0");	
		}
	}
	
	public void populatePlayoffs(Socket session_socket,PrintWriter print_writer,String viz_scene,List<Playoff> playoffs,List<Team> team,List<VariousText> vt, String broadcaster,Match match,int whichside) throws InterruptedException, IOException 
	{
		
		if(whichside == 1) {
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "*FUNCTION*Omo*vis_con SET 6\0");
		}else {
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "*FUNCTION*Omo*vis_con SET 6\0");
		}
		
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$txt_Header*GEOM*TEXT SET " + "HERO CLUB PLAYOFFS"  + "\0");
		print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET " + "AFC QUALIFICATIONS"  + "\0");
		for(VariousText vartext : vt) {
			if(vartext.getVariousType().equalsIgnoreCase("PLAYOFFSFOOTER") && vartext.getUseThis().toUpperCase().equalsIgnoreCase(RugbyUtil.YES)) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$BottomInfoGrp$txt_Info*GEOM*TEXT SET " + vartext.getVariousText()  + "\0");
			}else if(vartext.getVariousType().equalsIgnoreCase("PLAYOFFSFOOTER") && vartext.getUseThis().toUpperCase().equalsIgnoreCase(RugbyUtil.NO)) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$BottomInfoGrp$txt_Info*GEOM*TEXT SET " + 
							""  + "\0");
			}
		}
		print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$BottomInfoGrp$txt_Info*GEOM*TEXT SET " + 
				""  + "\0");
		print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$All_FF_Required$HashTag*ACTIVE SET 0\0");
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group1$GroupNameGrp$txt_MatchNumber*GEOM*TEXT SET " + playoffs.get(0).getPlayoffType()  + "\0");
		print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group2$GroupNameGrp$txt_MatchNumber*GEOM*TEXT SET " + playoffs.get(1).getPlayoffType()  + "\0");
		print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group3$GroupNameGrp$txt_MatchNumber*GEOM*TEXT SET " + playoffs.get(2).getPlayoffType()  + "\0");
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group1$txt_Date*ACTIVE SET 0\0");
		print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group2$txt_Date*ACTIVE SET 0\0");
		print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group3$txt_Date*ACTIVE SET 0\0");
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group1$txt_Date*GEOM*TEXT SET " + ""  + "\0");
		print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group2$txt_Date*GEOM*TEXT SET " + ""  + "\0");
		print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group3$txt_Date*GEOM*TEXT SET " + ""  + "\0");
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group1$TeamData1$TeamAll$Team$txt_TeamName*GEOM*TEXT SET " + playoffs.get(0).getTeam1().toUpperCase()  + "\0");
		print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group1$TeamData2$TeamAll$Team$txt_TeamName*GEOM*TEXT SET " + playoffs.get(0).getTeam2().toUpperCase()  + "\0");
		
		if(playoffs.get(0).getMargin() != null) {
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group1$TeamData1$TeamAll$Team$txt_TeamScore*GEOM*TEXT SET " + playoffs.get(0).getMargin().split("-")[0]  + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group1$TeamData2$TeamAll$Team$txt_TeamScore*GEOM*TEXT SET " + playoffs.get(0).getMargin().split("-")[1]  + "\0");
		}else {
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group1$TeamData1$TeamAll$Team$txt_TeamScore*GEOM*TEXT SET " + ""  + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group1$TeamData2$TeamAll$Team$txt_TeamScore*GEOM*TEXT SET " + ""  + "\0");
		}
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group2$TeamData1$TeamAll$Team$txt_TeamName*GEOM*TEXT SET " + playoffs.get(1).getTeam1().toUpperCase()  + "\0");
		print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group2$TeamData2$TeamAll$Team$txt_TeamName*GEOM*TEXT SET " + playoffs.get(1).getTeam2().toUpperCase()  + "\0");
		
		if(playoffs.get(1).getMargin() != null) {
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group2$TeamData1$TeamAll$Team$txt_TeamScore*GEOM*TEXT SET " + playoffs.get(1).getMargin().split("-")[0]  + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group2$TeamData2$TeamAll$Team$txt_TeamScore*GEOM*TEXT SET " + playoffs.get(1).getMargin().split("-")[1]  + "\0");
		}else {
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group2$TeamData1$TeamAll$Team$txt_TeamScore*GEOM*TEXT SET " + ""  + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group2$TeamData2$TeamAll$Team$txt_TeamScore*GEOM*TEXT SET " + ""  + "\0");
		}
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group3$TeamData1$TeamAll$Team$txt_TeamName*GEOM*TEXT SET " + playoffs.get(2).getTeam1().toUpperCase()  + "\0");
		print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group3$TeamData2$TeamAll$Team$txt_TeamName*GEOM*TEXT SET " + playoffs.get(2).getTeam2().toUpperCase()  + "\0");
		
		if(playoffs.get(2).getMargin() != null) {
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group3$TeamData1$TeamAll$Team$txt_TeamScore*GEOM*TEXT SET " + playoffs.get(2).getMargin().split("-")[0]  + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group3$TeamData2$TeamAll$Team$txt_TeamScore*GEOM*TEXT SET " + playoffs.get(2).getMargin().split("-")[1]  + "\0");
		}else {
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group3$TeamData1$TeamAll$Team$txt_TeamScore*GEOM*TEXT SET " + ""  + "\0");
			print_writer.println("-1 RENDERER*TREE*$Main$All$Side" + whichside + "$PlayOffs$AllData$Group3$TeamData2$TeamAll$Team$txt_TeamScore*GEOM*TEXT SET " + ""  + "\0");
		}
		
		print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_scene + " C:/Temp/Preview.png In 0.020 FF_In 2.000 PlayOffs_In 2.700 \0");
		
	}
	
	public void populateMatchResult(Socket session_socket,String viz_sence_path,int match_number,List<Fixture> fix,List<Team> team,List<Ground> ground,String session_selected_broadcaster,Match match) throws InterruptedException, IOException 
	{		
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		print_writer.println("-1 RENDERER*TREE*$Main$All$Select*FUNCTION*Omo*vis_con SET " + "2" + "\0");
		
		for(Team TM : team) {
			if(fix.get(match_number - 1).getHometeamid() == TM.getTeamId()) {

				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_1$img_LogoBW"
						+ "*TEXTURE*IMAGE SET "+ logo_bw_path + TM.getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_2$img_Logo"
						+ "*TEXTURE*IMAGE SET "+ logo_path + TM.getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_3$img_LogoOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + TM.getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HomeLogoGrp$Ani_4$img_LogoOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + TM.getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$txt_HomeTeam*GEOM*TEXT SET " + TM.getTeamName1().toUpperCase() + "\0");
			}
			if(fix.get(match_number - 1).getAwayteamid() == TM.getTeamId()) {
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_1$img_LogoBW"
						+ "*TEXTURE*IMAGE SET "+ logo_bw_path + TM.getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_2$img_Logo"
						+ "*TEXTURE*IMAGE SET "+ logo_path + TM.getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_3$img_LogoOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + TM.getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$AwayLogoGrp$Ani_4$img_LogoOutline"
						+ "*TEXTURE*IMAGE SET "+ logo_outline_path + TM.getTeamName4().toLowerCase() + "\0");
				print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$txt_AwayTeam*GEOM*TEXT SET " + TM.getTeamName1().toUpperCase() + "\0");
			}
		}
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$txt_Time*GEOM*TEXT SET " + "" + "\0");
		
		if(fix.get(match_number - 1).getMargin() != null) {
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$ScoreGtp$txt_Score*GEOM*TEXT SET " + fix.get(match_number - 1).getMargin() + "\0");
		}else {
			print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$ScoreGtp$txt_Score*GEOM*TEXT SET " + "" + "\0");
		}
		
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Home$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0");
		print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$Header$Dataall$Scorers$Away$Select*FUNCTION*Omo*vis_con SET " + "0" + "\0");
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$Select$ScoreLine$HeaderAll$SubHeader$txt_Info*GEOM*TEXT SET " + ground.get(fix.get(match_number -1).getVenue() - 1).getFullname() + "\0");
		
		
		print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_sence_path + " C:/Temp/Preview.png In 2.340 \0");
		
	}
	public void populateRoadToFinal(Socket session_socket,String viz_sence_path,List<LeagueTeam> point_table1,List<LeagueTeam> point_table2, List<Team> team,String session_selected_broadcaster,Match match) throws InterruptedException, IOException 
	{		
		
		print_writer = new PrintWriter(session_socket.getOutputStream(), true);
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$All_FF_Required$HashTag*ACTIVE SET 0 \0");
		
		
		int row_no_1=0,row_no_2=0,omo=0,l=4;
		String cout = "";
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$txt_Header*GEOM*TEXT SET " + "ROAD TO THE FINAL" + "\0");
		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$SubHeadGrp$txt_SubHead*GEOM*TEXT SET " + "GROUP STAGE" + "\0");
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style*FUNCTION*Omo*vis_con SET " + "1" + "\0");
		TimeUnit.MILLISECONDS.sleep(l);
		
		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$GroupHeadGrp$txt_Group*GEOM*TEXT SET " + "GROUP A" + "\0");
		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$GroupHeadGrp$txt_Group*GEOM*TEXT SET " + "GROUP B" + "\0");
		print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$BottomInfoGrp$txt_Info*GEOM*TEXT SET " + 
				"GROUP WINNERS QUALIFIED FOR THE SEMI-FINALS" + "\0");
		
		for(int i = 0; i <= point_table1.size() - 1 ; i++) {
			row_no_1 = row_no_1 + 1;
			
//			if(match.getHomeTeam().getTeamName2().equalsIgnoreCase(point_table1.get(i).getTeamName()) || 
//					match.getAwayTeam().getTeamName2().equalsIgnoreCase(point_table1.get(i).getTeamName())){
////				omo=1;
//				cout="$Highlight";
//			}else {
////				omo=0;
//				cout="$Dehighlight";
//			}
			
			for(Team tm : team) {
				if(point_table1.get(i).getTeamName().equalsIgnoreCase("BENGALURU")) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row1"
							+ "$SelectType*FUNCTION*Omo*vis_con SET 1 \0");
					if(point_table1.get(i).getQualifiedStatus().trim().equalsIgnoreCase("")) {
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
								"$SelectType$Highlight$Text$txt_Rank*GEOM*TEXT SET " + row_no_1 + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
								"$SelectType$Highlight$Text$txt_TeamName*GEOM*TEXT SET " + "BENGALURU FC" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
								"$SelectType$Highlight$Text$txt_Rank*GEOM*TEXT SET " + row_no_1 + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
								"$SelectType$Highlight$Text$txt_TeamName*GEOM*TEXT SET " + "BENGALURU FC (Q)" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					TimeUnit.MILLISECONDS.sleep(l);
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
							"$SelectType$Highlight$Text$PointsData$txt_PlayedValue*GEOM*TEXT SET " + point_table1.get(i).getPlayed() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
							"$SelectType$Highlight$Text$PointsData$txt_WinValue*GEOM*TEXT SET " + point_table1.get(i).getWon() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
							"$SelectType$Highlight$Text$PointsData$txt_DrawValue*GEOM*TEXT SET " + point_table1.get(i).getLost() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
							"$SelectType$Highlight$Text$PointsData$txt_LostValue*GEOM*TEXT SET " + point_table1.get(i).getDrawn() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
							"$SelectType$Highlight$Text$PointsData$txt_GoalDifferenceValue*GEOM*TEXT SET " + point_table1.get(i).getGD() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
							"$SelectType$Highlight$Text$PointsData$txt_PointsValue*GEOM*TEXT SET " + point_table1.get(i).getPoints() + "\0");
					
				}else if(tm.getTeamName1().contains(point_table1.get(i).getTeamName())) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
							"$SelectType*FUNCTION*Omo*vis_con SET 0 \0");
					if(point_table1.get(i).getQualifiedStatus().trim().equalsIgnoreCase("")) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
								"$SelectType$Dehighlight$Text$txt_Rank*GEOM*TEXT SET " + row_no_1 + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
								"$SelectType$Dehighlight$Text$txt_TeamName*GEOM*TEXT SET " + tm.getTeamName1().toUpperCase() + "\0");
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
								"$SelectType$Dehighlight$Text$txt_Rank*GEOM*TEXT SET " + row_no_1 + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
								"$SelectType$Dehighlight$Text$txt_TeamName*GEOM*TEXT SET " + tm.getTeamName1().toUpperCase() + " (Q)" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					TimeUnit.MILLISECONDS.sleep(l);
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
							"$SelectType$Dehighlight$Text$PointsData$txt_PlayedValue*GEOM*TEXT SET " + point_table1.get(i).getPlayed() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
							"$SelectType$Dehighlight$Text$PointsData$txt_WinValue*GEOM*TEXT SET " + point_table1.get(i).getWon() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
							"$SelectType$Dehighlight$Text$PointsData$txt_DrawValue*GEOM*TEXT SET " + point_table1.get(i).getLost() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
							"$SelectType$Dehighlight$Text$PointsData$txt_LostValue*GEOM*TEXT SET " + point_table1.get(i).getDrawn() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
							"$SelectType$Dehighlight$Text$PointsData$txt_GoalDifferenceValue*GEOM*TEXT SET " + point_table1.get(i).getGD() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData1$Row" + row_no_1 + 
							"$SelectType$Dehighlight$Text$PointsData$txt_PointsValue*GEOM*TEXT SET " + point_table1.get(i).getPoints() + "\0");
				}
			}
			
			

		}
		
		for(int i = 0; i <= point_table2.size() - 1 ; i++) {
			row_no_2 = row_no_2 + 1;
			
//			if(match.getHomeTeam().getTeamName2().equalsIgnoreCase(point_table2.get(i).getTeamName()) || 
//					match.getAwayTeam().getTeamName2().equalsIgnoreCase(point_table2.get(i).getTeamName())){
////				omo=1;
//				cout="$Highlight";
//			}else {
////				omo=0;
//				cout="$Dehighlight";
//			}
			
			for(Team tm : team) {
				if(point_table2.get(i).getTeamName().equalsIgnoreCase("ODISHA")) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row1"
							+ "$SelectType*FUNCTION*Omo*vis_con SET 1 \0");
					if(point_table2.get(i).getQualifiedStatus().trim().equalsIgnoreCase("")) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
								"$SelectType$Highlight$Text$txt_Rank*GEOM*TEXT SET " + row_no_2 + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
								"$SelectType$Highlight$Text$txt_TeamName*GEOM*TEXT SET " + "ODISHA FC" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
								"$SelectType$Highlight$Text$txt_Rank*GEOM*TEXT SET " + row_no_2 + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
								"$SelectType$Highlight$Text$txt_TeamName*GEOM*TEXT SET " + "ODISHA FC (Q)" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
							"$SelectType$Highlight$Text$PointsData$txt_PlayedValue*GEOM*TEXT SET " + point_table2.get(i).getPlayed() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
							"$SelectType$Highlight$Text$PointsData$txt_WinValue*GEOM*TEXT SET " + point_table2.get(i).getWon() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
							"$SelectType$Highlight$Text$PointsData$txt_DrawValue*GEOM*TEXT SET " + point_table2.get(i).getLost() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
							"$SelectType$Highlight$Text$PointsData$txt_LostValue*GEOM*TEXT SET " + point_table2.get(i).getDrawn() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
							"$SelectType$Highlight$Text$PointsData$txt_GoalDifferenceValue*GEOM*TEXT SET " + point_table2.get(i).getGD() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
							"$SelectType$Highlight$Text$PointsData$txt_PointsValue*GEOM*TEXT SET " + point_table2.get(i).getPoints() + "\0");
					
				}else if(tm.getTeamName1().contains(point_table2.get(i).getTeamName())) {
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
							"$SelectType*FUNCTION*Omo*vis_con SET 0 \0");
					if(point_table2.get(i).getQualifiedStatus().trim().equalsIgnoreCase("")) {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
								"$SelectType$Dehighlight$Text$txt_Rank*GEOM*TEXT SET " + row_no_2 + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
								"$SelectType$Dehighlight$Text$txt_TeamName*GEOM*TEXT SET " + tm.getTeamName1().toUpperCase() + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}else {
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
								"$SelectType$Dehighlight$Text$txt_Rank*GEOM*TEXT SET " + row_no_2 + "\0");
						print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
								"$SelectType$Dehighlight$Text$txt_TeamName*GEOM*TEXT SET " + tm.getTeamName1().toUpperCase() + " (Q)" + "\0");
						TimeUnit.MILLISECONDS.sleep(l);
					}
					TimeUnit.MILLISECONDS.sleep(l);
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
							"$SelectType$Dehighlight$Text$PointsData$txt_PlayedValue*GEOM*TEXT SET " + point_table2.get(i).getPlayed() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
							"$SelectType$Dehighlight$Text$PointsData$txt_WinValue*GEOM*TEXT SET " + point_table2.get(i).getWon() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
							"$SelectType$Dehighlight$Text$PointsData$txt_DrawValue*GEOM*TEXT SET " + point_table2.get(i).getLost() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
							"$SelectType$Dehighlight$Text$PointsData$txt_LostValue*GEOM*TEXT SET " + point_table2.get(i).getDrawn() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
							"$SelectType$Dehighlight$Text$PointsData$txt_GoalDifferenceValue*GEOM*TEXT SET " + point_table2.get(i).getGD() + "\0");
					
					print_writer.println("-1 RENDERER*TREE*$Main$All$All_FullFRames$PointsTableAll$AllData$Select_PointsData_style$Group2$PointsData2$Row" + row_no_2 + 
							"$SelectType$Dehighlight$Text$PointsData$txt_PointsValue*GEOM*TEXT SET " + point_table2.get(i).getPoints() + "\0");
				}
			}
			
			

		}
		print_writer.println("-1 RENDERER PREVIEW SCENE*" + viz_sence_path + " C:/Temp/Preview.png In 0.020 FF_In 2.000 PointsTable_In 2.580 \0");
		
	}
}
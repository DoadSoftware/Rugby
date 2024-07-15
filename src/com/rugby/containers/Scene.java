package com.rugby.containers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import com.rugby.util.RugbyUtil;

public class Scene {
	
	private String scene_path;
	private String broadcaster;
	private String which_layer;
	
	public Scene() {
		super();
	}

	public Scene(String scene_path, String which_layer) {
		super();
		this.scene_path = scene_path;
		this.which_layer = which_layer;
	}
	
	public String getScene_path() {
		return scene_path;
	}

	public void setScene_path(String scene_path) {
		this.scene_path = scene_path;
	}
	
	public String getBroadcaster() {
		return broadcaster;
	}

	public void setBroadcaster(String broadcaster) {
		this.broadcaster = broadcaster;
	}

	public String getWhich_layer() {
		return which_layer;
	}

	public void setWhich_layer(String which_layer) {
		this.which_layer = which_layer;
	}

	public void scene_load(Socket socket, String broadcaster) throws InterruptedException, IOException
	{
		PrintWriter print_writer = new PrintWriter(socket.getOutputStream(), true);
		switch (broadcaster.toUpperCase()) {
		case RugbyUtil.I_LEAGUE: case RugbyUtil.SANTOSH_TROPHY: case RugbyUtil.KHELO_INDIA:case RugbyUtil.NATIONALS:
			switch(this.which_layer) {
			case RugbyUtil.ONE:
				//System.out.println("Secne : " + this.scene_path);
				print_writer.println("LAYER1*EVEREST*SINGLE_SCENE LOAD " + this.scene_path + ";");
				
				print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
				print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
				TimeUnit.MILLISECONDS.sleep(500);
				break;
			case RugbyUtil.TWO:
				print_writer.println("LAYER2*EVEREST*SINGLE_SCENE LOAD " + this.scene_path + ";");
				
				print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
				print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
				TimeUnit.MILLISECONDS.sleep(500);
				break;
			}
			break;
		case RugbyUtil.VIZ_SANTOSH_TROPHY: case RugbyUtil.VIZ_TRI_NATION: case RugbyUtil.SUPER_CUP: case RugbyUtil.CONTINENTAL:
			switch(this.which_layer.toUpperCase()) {
			case RugbyUtil.FRONT_LAYER:
				print_writer.println("-1 RENDERER*FRONT_LAYER SET_OBJECT SCENE*" + this.scene_path + "\0");
				//print_writer.println("-1 RENDERER*FRONT_LAYER INITIALIZE \0");
				print_writer.println("-1 RENDERER*FRONT_LAYER*SCENE_DATA INITIALIZE \0");
				//print_writer.println("-1 RENDERER*FRONT_LAYER*UPDATE SET 1");
				TimeUnit.MILLISECONDS.sleep(500);
				break;
			case RugbyUtil.MIDDLE_LAYER:
				print_writer.println("-1 RENDERER SET_OBJECT SCENE*" + this.scene_path + "\0");
				print_writer.println("-1 RENDERER*STAGE SHOW 0.0\0");
				//print_writer.println("-1 RENDERER INITIALIZE \0");
				print_writer.println("-1 RENDERER*SCENE_DATA INITIALIZE \0");
				//print_writer.println("-1 RENDERER*UPDATE SET 1");
				TimeUnit.MILLISECONDS.sleep(500);
				break;
			}
			break;
			
		}
	}
}

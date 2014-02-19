package com.me.boxing;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Serjay Story";
		cfg.useGL20 = true;
	//	cfg.fullscreen = true;
		cfg.width = 1280;
		cfg.height = 720;
		cfg.addIcon("icon.png", FileType.Internal);
		
		new LwjglApplication(new Boxing(), cfg);
	}
}

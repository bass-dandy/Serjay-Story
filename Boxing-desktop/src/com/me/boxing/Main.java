package com.me.boxing;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Serjay Story";
		cfg.useGL20 = true;
		cfg.width = 1200;
		cfg.height = 675;
		cfg.addIcon("icon.png", FileType.Internal);
		
		new LwjglApplication(new Boxing(), cfg);
	}
}

package net.killsay;

import java.util.*;

/**
 * Reconstructed audit source, not original source.
 */
public class Config {
    public boolean enabled = false;
    public List<String> templates = new ArrayList<>(List.of("{name} ���Ҵ�����"));
    public double cooldownSeconds = 3.0D;
    public double windowSeconds = 5.0D;
    public boolean debug = false;

    public int webGuiPort = 27563;
    public boolean joinEnabled = false;
    public List<String> joinTemplates = new ArrayList<>(List.of("{name} ��������Ϸ"));
    public String joinSendMode = "first";
    public boolean winEnabled = false;
    public List<String> winTemplates = new ArrayList<>(List.of("Ӯ�ˣ�"));
    public String winSendMode = "first";
    public String sendMode = "first";

    record ProfilesData(String current, List<String> profiles) {}
}


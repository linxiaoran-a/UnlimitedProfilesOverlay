package com.agent.profilesui;

public class Ctl {
    private static final String CTL = "/data/adb/modules/unlimited_profiles_overlay/webui_ctl.sh";

    public static class Status {
        public int limit;
        public String fw = "";
        public String ds = "";
        public String l1 = "";
        public String l2 = "";
        public int userCount;
        public String users = "";
        public String ovs = "";
    }

    private static String ctl(String args) throws Exception {
        return RootShell.exec("sh " + CTL + " " + args);
    }

    public static Status status() throws Exception {
        String json = ctl("status");
        Status s = new Status();
        s.limit = extractInt(json, "limit");
        s.fw = extractString(json, "fw");
        s.ds = extractString(json, "ds");
        s.l1 = extractString(json, "l1");
        s.l2 = extractString(json, "l2");
        s.userCount = extractInt(json, "user_count");
        s.users = extractString(json, "users");
        s.ovs = extractString(json, "ovs");
        return s;
    }

    public static void set(int limit) throws Exception {
        ctl("set " + limit);
    }

    public static void apply() throws Exception {
        ctl("apply");
    }

    private static int extractInt(String json, String key) {
        String pat = "\"" + key + "\":";
        int i = json.indexOf(pat);
        if (i < 0) return 0;
        i += pat.length();
        int j = i;
        while (j < json.length() && (Character.isDigit(json.charAt(j)) || json.charAt(j) == '-')) j++;
        try {
            return Integer.parseInt(json.substring(i, j));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String extractString(String json, String key) {
        String pat = "\"" + key + "\":\"";
        int i = json.indexOf(pat);
        if (i < 0) return "";
        i += pat.length();
        StringBuilder sb = new StringBuilder();
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char n = json.charAt(i + 1);
                if (n == 'n') sb.append('\n');
                else if (n == 't') sb.append('\t');
                else sb.append(n);
                i += 2;
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
                i++;
            }
        }
        return sb.toString();
    }
}

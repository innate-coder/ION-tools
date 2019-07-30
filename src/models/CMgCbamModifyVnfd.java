package models;


import java.util.ArrayList;

public class CMgCbamModifyVnfd implements MainModelClass {

    ArrayList<Extentions> extensions;
    String apiVersion;

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public ArrayList<Extentions> getExtensions() {
        return extensions;
    }

    public void setExtensions(ArrayList<Extentions> extensions) {
        this.extensions = extensions;
    }

    public static class Extentions {
        private String name;
        private String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            String data = name + " : " + value;
            return data;
        }
    }

    @Override
    public String toString() {
        if (getApiVersion() != null && getExtensions() != null) {
            return "apiVersion=" + getApiVersion() + "\nList is " + getExtensions().toString();
        } else {
            return "";
        }
    }
}

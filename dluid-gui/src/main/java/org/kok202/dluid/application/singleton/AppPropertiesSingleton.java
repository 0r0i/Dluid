package org.kok202.dluid.application.singleton;

import org.kok202.dluid.application.Main;

import java.util.Properties;

public class AppPropertiesSingleton {
    private static final AppPropertiesSingleton INSTANCE = new AppPropertiesSingleton();
    private Properties commonProperties;
    private Properties languageProperties;
    private AppPropertiesSingleton(){
        try {
            commonProperties = new Properties();
            commonProperties.load(Main.class.getClassLoader().getResourceAsStream("application.properties"));
            languageProperties = new Properties();
            languageProperties.load(Main.class.getClassLoader().getResourceAsStream(getLanguagePropertiesPath()));
        }catch (Exception e){
            commonProperties = null;
            languageProperties = null;
        }
    }

    private String getLanguagePropertiesPath(){
        System.out.println(AppConfigurationSingleton.getInstance().getData().getLanguage());
        AppConfigurationSingleton.getInstance().getData().setLanguage("en");
        AppConfigurationSingleton.getInstance().saveData();
        switch (AppConfigurationSingleton.getInstance().getData().getLanguage()){
            case "kor":
                return "application-kor.properties";
            default:
                return "application-en.properties";
        }
    }

    public static AppPropertiesSingleton getInstance(){
        return INSTANCE;
    }

    public String get(String key) {
        String retValue = "";
        if(languageProperties != null)
            retValue = languageProperties.getProperty(key);
        if(retValue == null || retValue.isEmpty())
            retValue = commonProperties.getProperty(key);
        return retValue;
    }

    public int getInt(String key) {
        int retValue = Integer.MAX_VALUE;
        if(languageProperties != null && languageProperties.getProperty(key) != null)
            retValue = Integer.parseInt(languageProperties.getProperty(key));
        if(retValue == Integer.MAX_VALUE && commonProperties.getProperty(key) !=null)
            retValue = Integer.parseInt(commonProperties.getProperty(key));
        return retValue;
    }
}

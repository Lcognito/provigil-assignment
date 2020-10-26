/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.provigil.handler.data;

import com.provigil.models.Plan;
import com.provigil.models.SquareFootage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



/**
 *
 * @author Kalyan
 */
public class PlansHandler {

    private static PlansHandler inst = null;
    private boolean plansBuilt = false;

    // The plans need to be in sorted order based on Sq. Ft.
    private TreeMap<SquareFootage, Plan> indoorPlans = null;
    private TreeMap<SquareFootage, Plan> outdoorPlans = null;


    private PlansHandler() {
        indoorPlans = new TreeMap();
        outdoorPlans = new TreeMap();
    }

    public static PlansHandler getInstance() {
        if (inst == null)
            inst = new PlansHandler();

        return inst;
    }

    public void buildPlans() {
        JSONParser parser = new JSONParser();


        FileReader fileReader = null;

        File indoorDirectory = new File(System.getProperty("user.dir") + "\\indoor\\");
        File outdoorDirectory = new File(System.getProperty("user.dir") + "\\outdoor\\");

        File[] indoorPlansFiles = indoorDirectory.listFiles();
        File[] outdoorPlansFiles = outdoorDirectory.listFiles();

        for (File indoorFile : indoorPlansFiles) {
            try {
                fileReader = new FileReader(indoorFile);

                JSONObject jsonObj = (JSONObject)parser.parse(fileReader);

                Plan planObj = new Plan();
                planObj.setLocation((String)jsonObj.get("location"));
                planObj.setSq_Ft((String)jsonObj.get("sq_ft"));


                Map sur_cost = (Map)jsonObj.get("sur_cost");
                Iterator<Map.Entry<String, String>> itr1 = sur_cost.entrySet().iterator();
                while (itr1.hasNext()) {
                    Map.Entry<String, String> pair = itr1.next();
                    if (pair.getKey().equalsIgnoreCase("monthly"))
                        planObj.setMonthly_sur_cost(Double.parseDouble(pair.getValue()));
                    else
                        planObj.setYearly_sur_cost(Double.parseDouble(pair.getValue()));
                }


                switch(planObj.getSq_Ft()) {

                    case "<=2500":
                        planObj.setMax_sq_ft(2500);
                        planObj.setMin_sq_ft(1);
                        indoorPlans.put(SquareFootage.LE_2500, planObj);
                        break;

                    case "2501-5000":
                        planObj.setMax_sq_ft(5000);
                        planObj.setMin_sq_ft(2501);
                        indoorPlans.put(SquareFootage.F_2501_T_5000, planObj);
                        break;

                    case "5001-50000":
                        planObj.setMax_sq_ft(50000);
                        planObj.setMin_sq_ft(5001);
                        indoorPlans.put(SquareFootage.F_5001_T_50000, planObj);
                        break;

                    case "50001+":
                        planObj.setMax_sq_ft(Double.MAX_VALUE);
                        indoorPlans.put(SquareFootage.F_50001_PLUS, planObj);
                        break;
                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(PlansHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PlansHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(PlansHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        for (File outdoorFile : outdoorPlansFiles) {
            try {
                fileReader = new FileReader(outdoorFile);

                JSONObject jsonObj = (JSONObject)parser.parse(fileReader);

                Plan planObj = new Plan();
                planObj.setLocation((String)jsonObj.get("location"));
                planObj.setSq_Ft((String)jsonObj.get("sq_ft"));

                Map sur_cost = (Map)jsonObj.get("sur_cost");
                Iterator<Map.Entry<String, String>> itr1 = sur_cost.entrySet().iterator();
                while (itr1.hasNext()) {
                    Map.Entry<String, String> pair = itr1.next();
                    if (pair.getKey().equalsIgnoreCase("monthly"))
                        planObj.setMonthly_sur_cost(Double.parseDouble(pair.getValue()));
                    else
                        planObj.setYearly_sur_cost(Double.parseDouble(pair.getValue()));
                }

                switch(planObj.getSq_Ft()) {

                    case "<=2500":
                        planObj.setMax_sq_ft(2500);
                        planObj.setMin_sq_ft(1);
                        outdoorPlans.put(SquareFootage.LE_2500, planObj);
                        break;

                    case "2501-5000":
                        planObj.setMax_sq_ft(5000);
                        planObj.setMin_sq_ft(2501);
                        outdoorPlans.put(SquareFootage.F_2501_T_5000, planObj);
                        break;

                    case "5001-50000":
                        planObj.setMax_sq_ft(50000);
                        planObj.setMin_sq_ft(5001);
                        outdoorPlans.put(SquareFootage.F_5001_T_50000, planObj);
                        break;

                    case "50001+":
                        planObj.setMax_sq_ft(Double.MAX_VALUE);
                        outdoorPlans.put(SquareFootage.F_50001_PLUS, planObj);
                        break;
                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(PlansHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PlansHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(PlansHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        plansBuilt = true;

    }

    public TreeMap<SquareFootage, Plan> getAllIndoorPlans() {
        return indoorPlans;
    }

    public TreeMap<SquareFootage, Plan> getAllOutdoorPlans() {
        return outdoorPlans;
    }

    public boolean arePlansAvailable() {
        return plansBuilt;
    }
}

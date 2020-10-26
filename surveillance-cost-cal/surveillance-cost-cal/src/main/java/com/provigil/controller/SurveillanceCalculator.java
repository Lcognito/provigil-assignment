/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.provigil.controller;

import com.provigil.handler.data.PlansHandler;
import com.provigil.models.Plan;
import com.provigil.models.Result;
import com.provigil.models.SquareFootage;
import com.provigil.models.Subscription;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Kalyan
 */
public class SurveillanceCalculator {

    private PlansHandler plansHandler;

    public SurveillanceCalculator() {
        plansHandler = PlansHandler.getInstance();
        plansHandler.buildPlans();
    }

    public ArrayList<Result> calculateSubscription(ArrayList<Subscription> collection) {
        ArrayList<Result> resultsList = new ArrayList();

        for (Subscription sbs : collection) {
            Result result = new Result();

            result.setId(sbs.getId());
            double area = Double.parseDouble(sbs.getArea());

            if(sbs.getLocation().equalsIgnoreCase("indoor")) {

                if (sbs.getPlan().equalsIgnoreCase("monthly")) {
                    calculateIndoorMonthly(area, result);
                }

                if (sbs.getPlan().equalsIgnoreCase("yearly")) {
                    calculateIndoorYearly(area, result);
                }
            }

            if(sbs.getLocation().equalsIgnoreCase("outdoor")) {

                if (sbs.getPlan().equalsIgnoreCase("monthly")) {
                    calculateOutdoorMonthly(area, result);
                }

                if (sbs.getPlan().equalsIgnoreCase("yearly")) {
                    calculateOutdoorYearly(area, result);
                }
            }

            if (result.getCost() != null && !result.getCost().isEmpty())
                resultsList.add(result);
        }

        return resultsList;

    }

    private void calculateIndoorMonthly(double area, Result result) {
        TreeMap<SquareFootage, Plan> indoorPls = plansHandler.getAllIndoorPlans();
        double cummulativeCost = 0, maxArea = 0, offsetArea = 0;

        for (Map.Entry<SquareFootage, Plan> entry : indoorPls.entrySet()) {
            Plan plan = entry.getValue();
            maxArea = plan.getMax_sq_ft();

            if (area == 0)
                break;
            if (area >= maxArea) {
                offsetArea = plan.getMax_sq_ft() - plan.getMin_sq_ft();
                cummulativeCost += ((offsetArea + 1) * plan.getMonthly_sur_cost());
                area -= offsetArea + 1;
            } else {
                cummulativeCost += area * plan.getMonthly_sur_cost();
                break;
            }
        }

        result.setCost(String.valueOf(cummulativeCost));
    }

    private void calculateIndoorYearly(double area, Result result) {
        TreeMap<SquareFootage, Plan> indoorPls = plansHandler.getAllIndoorPlans();
        double cummulativeCost = 0, maxArea = 0, offsetArea = 0;

        for (Map.Entry<SquareFootage, Plan> entry : indoorPls.entrySet()) {
            Plan plan = entry.getValue();
            maxArea = plan.getMax_sq_ft();

            if (area == 0)
                break;
            if (area >= maxArea) {
                offsetArea = plan.getMax_sq_ft() - plan.getMin_sq_ft();
                cummulativeCost += ((offsetArea + 1) * plan.getYearly_sur_cost());
                area -= offsetArea + 1;
            } else {
                cummulativeCost += area * plan.getYearly_sur_cost();
                break;
            }
        }

        result.setCost(String.valueOf(cummulativeCost));
    }

    private void calculateOutdoorMonthly(double area, Result result) {
        TreeMap<SquareFootage, Plan> outdoorPls = plansHandler.getAllOutdoorPlans();
        double cummulativeCost = 0, maxArea = 0, offsetArea = 0;

        for (Map.Entry<SquareFootage, Plan> entry : outdoorPls.entrySet()) {
            Plan plan = entry.getValue();
            maxArea = plan.getMax_sq_ft();

            if (area == 0)
                break;
            if (area >= maxArea) {
                offsetArea = plan.getMax_sq_ft() - plan.getMin_sq_ft();
                cummulativeCost += ((offsetArea + 1) * plan.getMonthly_sur_cost());
                area -= offsetArea + 1;
            } else {
                cummulativeCost += area * plan.getMonthly_sur_cost();
                break;
            }
        }

        result.setCost(String.valueOf(cummulativeCost));
    }

    private void calculateOutdoorYearly(double area, Result result) {
        TreeMap<SquareFootage, Plan> outdoorPls = plansHandler.getAllOutdoorPlans();
        double cummulativeCost = 0, maxArea = 0, offsetArea = 0;

        for (Map.Entry<SquareFootage, Plan> entry : outdoorPls.entrySet()) {
            Plan plan = entry.getValue();
            maxArea = plan.getMax_sq_ft();

            if (area == 0)
                break;
            if (area >= maxArea) {
                offsetArea = plan.getMax_sq_ft() - plan.getMin_sq_ft();
                cummulativeCost += ((offsetArea + 1) * plan.getYearly_sur_cost());
                area -= offsetArea + 1;
            } else {
                cummulativeCost += area * plan.getYearly_sur_cost();
                break;
            }
        }

        result.setCost(String.valueOf(cummulativeCost));
    }

}

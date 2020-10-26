/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.provigil.models;

/**
 *
 * @author Kalyan
 */
public class Plan {

    private String location;
    private String sq_Ft;
    private double monthly_sur_cost;
    private double yearly_sur_cost;
    private double max_sq_ft;
    private double min_sq_ft;

    public double getMin_sq_ft() {
        return min_sq_ft;
    }

    public void setMin_sq_ft(double min_sq_ft) {
        this.min_sq_ft = min_sq_ft;
    }

    
    public double getMonthly_sur_cost() {
        return monthly_sur_cost;
    }

    public void setMonthly_sur_cost(double monthly_sur_cost) {
        this.monthly_sur_cost = monthly_sur_cost;
    }

    public double getYearly_sur_cost() {
        return yearly_sur_cost;
    }

    public void setYearly_sur_cost(double yearly_sur_cost) {
        this.yearly_sur_cost = yearly_sur_cost;
    }


    public double getMax_sq_ft() {
        return max_sq_ft;
    }

    public void setMax_sq_ft(double max_sq_ft) {
        this.max_sq_ft = max_sq_ft;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSq_Ft() {
        return sq_Ft;
    }

    public void setSq_Ft(String sq_Ft) {
        this.sq_Ft = sq_Ft;
    }

}

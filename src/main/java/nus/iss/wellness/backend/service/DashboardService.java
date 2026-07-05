package nus.iss.wellness.backend.service;


import nus.iss.wellness.backend.dto.response.DashboardResponse;

//Author: Cecil

public interface DashboardService {

    DashboardResponse getDashboard(Long userId);

}
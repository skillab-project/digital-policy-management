package gr.uom.strategicplanning.controllers;

import gr.uom.strategicplanning.controllers.entities.IndicatorUpdateReport;
import gr.uom.strategicplanning.models.User;
import gr.uom.strategicplanning.services.IndicatorReportService;
import gr.uom.strategicplanning.services.MetricReportService;
import gr.uom.strategicplanning.services.UserPrivilegedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin")
public class UserPrivilegedController {

    @Autowired
    UserPrivilegedService userPrivilegedService;

    @Autowired
    IndicatorReportService indicatorReportService;


    @PutMapping("/verify")
    User verifyUser(@RequestParam String email){
        return userPrivilegedService.verifyUser(email);
    }

    @PutMapping("/authorize")
    User givePrivilegeToUser(@RequestParam String email){
        return userPrivilegedService.givePrivilegeToUser(email);
    }

    @DeleteMapping("delete/user")
    void deleteUser(@RequestParam String email) {
        userPrivilegedService.deleteUser(email);
    }

    @DeleteMapping("delete/indicatorReport")
    void deleteIndicatorReport(@RequestParam IndicatorUpdateReport indicatorUpdateReport) {
        indicatorReportService.deleteIndicatorReport(indicatorUpdateReport);
    }
}

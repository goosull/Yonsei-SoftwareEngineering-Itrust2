package edu.ncsu.csc.itrust2.controllers.routing;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.ncsu.csc.itrust2.models.enums.Role;

/**
 * Controller to manage basic abilities for ER roles
 *
 * @author Amalan Iyengar
 *
 */

@Controller
public class ERController {

    /**
     * Returns the ER for the given model
     *
     * @param model
     *            model to check
     * @return role
     */
    @RequestMapping ( value = "er/index" )
    @PreAuthorize ( "hasRole('ROLE_ER')" )
    public String index ( final Model model ) {
        return Role.ROLE_ER.getLanding();
    }

    /**
     * Returns the ER for the given model
     *
     * @param model
     *            model to check
     * @return role
     */
    @RequestMapping ( value = "er/records" )
    @PreAuthorize ( "hasRole('ROLE_ER')" )
    public String emergencyRecords ( final Model model ) {
        return "personnel/records";
    }

    /**
     * Returns the page for a ER to view Emergency Health Records
     *
     * @param model
     *            The data for the front end
     * @return Page to display to the user
     */
    @GetMapping ( "/er/viewEHR" )
    @PreAuthorize ( "hasRole('ROLE_ER')" )
    public String viewEHR ( final Model model ) {
        return "/er/viewEHR";
    }

}

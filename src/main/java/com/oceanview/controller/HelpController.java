package com.oceanview.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Help Controller - System documentation and user guide
 */
@WebServlet(name = "HelpController", urlPatterns = {"/help"})
public class HelpController extends BaseController {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        forwardToView(request, response, "/WEB-INF/views/help.jsp");
    }
}

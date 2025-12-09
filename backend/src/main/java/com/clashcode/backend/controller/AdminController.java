package com.clashcode.backend.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admins")
@PreAuthorize("hasRole('Admin')")
public class AdminController {
}

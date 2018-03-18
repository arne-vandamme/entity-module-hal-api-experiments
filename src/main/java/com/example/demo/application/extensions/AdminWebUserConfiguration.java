package com.example.demo.application.extensions;

import com.foreach.across.core.annotations.ModuleConfiguration;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

/**
 * A dummy Spring Security configuration that configures a single memory user that can access
 * the Administration UI (from AdminWebModule).  Check out the UserModule if you quickly want
 * to set up a simple user domain with groups, roles and permissions.
 */
@ModuleConfiguration(SpringSecurityModule.NAME)
public class AdminWebUserConfiguration
{
	@Autowired
	public void configureSingleFixedAdminUser( AuthenticationManagerBuilder auth ) throws Exception {
		auth.inMemoryAuthentication()
		    .withUser( "admin" ).password( "admin" )
		    .authorities( "access administration" );
	}
}
/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.profile.management.web.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.TenantService;
import org.craftercms.profile.management.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * MVC Controller for displaying and modifying tenants.
 *
 * @author avasquez
 */
@Controller
@RequestMapping(TenantController.BASE_URL_TENANT)
public class TenantController {

    public static final String PATH_VAR_NAME = "name";

    public static final String BASE_URL_TENANT = "/tenant";

    public static final String URL_VIEW_TENANT_LIST = "/list/view";
    public static final String URL_VIEW_CREATE_TENANT = "/new/view";
    public static final String URL_VIEW_UPDATE_TENANT = "/update/view";

    public static final String URL_GET_TENANT_NAMES = "/names";
    public static final String URL_GET_TENANT = "/{" + PATH_VAR_NAME + "}";
    public static final String URL_CREATE_TENANT = "/new";
    public static final String URL_UPDATE_TENANT = "/update";
    public static final String URL_DELETE_TENANT = "/{" + PATH_VAR_NAME + "}/delete";

    public static final String VIEW_TENANT_LIST = "tenant-list";
    public static final String VIEW_TENANT = "tenant";

    public static final String MODEL_PAGE_HEADER = "pageHeader";
    public static final String MODEL_MESSAGE = "message";

    public static final String PAGE_HEADER_CREATE = "New Tenant";
    public static final String PAGE_HEADER_UPDATE = "Update Tenant";

    public static final String MSG_TENANT_CREATED_FORMAT = "Tenant '%s' created";
    public static final String MSG_TENANT_UPDATED_FORMAT = "Tenant '%s' updated";
    public static final String MSG_TENANT_DELETED_FORMAT = "Tenant '%s' deleted";

    private TenantService tenantService;

    @Required
    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @RequestMapping(value = URL_VIEW_TENANT_LIST, method = RequestMethod.GET)
    public String viewTenantList() throws ProfileException {
        return VIEW_TENANT_LIST;
    }

    @RequestMapping(value = URL_VIEW_CREATE_TENANT, method = RequestMethod.GET)
    public ModelAndView viewCreateTenant() throws ProfileException {
        return new ModelAndView(VIEW_TENANT, MODEL_PAGE_HEADER, PAGE_HEADER_CREATE);
    }

    @RequestMapping(value = URL_VIEW_UPDATE_TENANT, method = RequestMethod.GET)
    public ModelAndView viewUpdateTenant() throws ProfileException {
        return new ModelAndView(VIEW_TENANT, MODEL_PAGE_HEADER, PAGE_HEADER_UPDATE);
    }

    @RequestMapping(value = URL_GET_TENANT_NAMES, method = RequestMethod.GET)
    @ResponseBody
    public List<String> getTenantNames() throws ProfileException {
        List<Tenant> tenants = tenantService.getAllTenants();
        List<String> tenantNames = new ArrayList<>(tenants.size());

        for (Tenant tenant : tenants) {
            tenantNames.add(tenant.getName());
        }

        return tenantNames;
    }

    @RequestMapping(value = URL_GET_TENANT, method = RequestMethod.GET)
    @ResponseBody
    public Tenant getTenant(@PathVariable(PATH_VAR_NAME) String name) throws ProfileException {
        Tenant tenant = tenantService.getTenant(name);
        if (tenant != null) {
            return tenant;
        } else {
            throw new ResourceNotFoundException("No tenant found with name '" + name + "'");
        }
    }

    @RequestMapping(value = URL_CREATE_TENANT, method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> createTenant(@RequestBody Tenant tenant) throws ProfileException {
        tenant = tenantService.createTenant(tenant);

        return Collections.singletonMap(MODEL_MESSAGE, String.format(MSG_TENANT_CREATED_FORMAT, tenant.getName()));
    }

    @RequestMapping(value = URL_UPDATE_TENANT, method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> updateTenant(@RequestBody Tenant tenant) throws ProfileException {
        tenant = tenantService.updateTenant(tenant);

        return Collections.singletonMap(MODEL_MESSAGE, String.format(MSG_TENANT_UPDATED_FORMAT, tenant.getName()));
    }

    @RequestMapping(value = URL_DELETE_TENANT, method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> deleteTenant(@PathVariable(PATH_VAR_NAME) String name) throws ProfileException {
        tenantService.deleteTenant(name);

        return Collections.singletonMap(MODEL_MESSAGE, String.format(MSG_TENANT_DELETED_FORMAT, name));
    }

}

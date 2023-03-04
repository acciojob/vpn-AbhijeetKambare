package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;


    @Override
    public Admin register(String username, String password) {
        Admin admin=new Admin();
        admin.setPassword(password);
        admin.setUsername(username);
       adminRepository1.save(admin);
        return admin;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        Admin admin=adminRepository1.findById(adminId).get();
        ServiceProvider serviceProvider=new ServiceProvider();
        serviceProvider.setName(providerName);
        serviceProvider.setAdmin(admin);
        admin.getServiceProviders().add(serviceProvider);
        return adminRepository1.save(admin);
    }
    public static final Map<String,CountryName> countryMap=new HashMap<>();
    static {
        countryMap.put("IND",CountryName.IND);
        countryMap.put("USA",CountryName.USA);
        countryMap.put("JPN",CountryName.JPN);
        countryMap.put("CHI",CountryName.CHI);
        countryMap.put("AUS",CountryName.AUS);
    }
    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception{
        ServiceProvider serviceProvider=serviceProviderRepository1.findById(serviceProviderId).get();
        String CName=countryName.toUpperCase();
        CountryName name=countryMap.get(CName);
        if(name!=null){
            Country country = new Country();
            country.setCountryName(name);
            country.setCode(name.toCode());
            country.setServiceProvider(serviceProvider);
            serviceProvider.getCountryList().add(country);

            return serviceProviderRepository1.save(serviceProvider);

        }
        else
            throw new Exception("Country not found");
    }
}

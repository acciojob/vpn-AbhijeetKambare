package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{
        User user=new User();
        user.setPassword(password);
        user.setUsername(username);
        CountryName name=AdminServiceImpl.countryMap.get(countryName);

        if (name == null) throw new Exception("Country not found");
        else {
            Country country = new Country();
            country.setCountryName(name);
            country.setCode(name.toCode());
            country.setUser(user);

            user.setOriginalCountry(country);
            user.setConnected(false);
            String ip = name.toCode() + "." + userRepository3.save(user).getId();
            user.setOriginalIp(ip);
            userRepository3.save(user);
        }
        return user;
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        User user=userRepository3.findById(userId).get();
        ServiceProvider serviceProvider=serviceProviderRepository3.findById(serviceProviderId).get();
        List<ServiceProvider> providers=user.getServiceProviderList();
        providers.add(serviceProvider);
        user.setServiceProviderList(providers);

        return userRepository3.save(user);
    }
}

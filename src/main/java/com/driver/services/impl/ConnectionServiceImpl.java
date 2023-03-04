package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        User user=userRepository2.findById(userId).get();
        if(user.getMaskedIp()!=null) throw new Exception("Already connected");
        else if (countryName.equalsIgnoreCase(user.getOriginalCountry().getCountryName().toString())) {
            return user; //do nothing;
        }
        else {
            if(user.getServiceProviderList()==null) throw new Exception("Unable to connect");
        }
        List<ServiceProvider> providers=user.getServiceProviderList();
        int a=Integer.MAX_VALUE;
        ServiceProvider serviceProvider=null;
        Country country=null;
        for (ServiceProvider provider:providers){
            List<Country> countries=provider.getCountryList();
            for (Country country1:countries){
                if(countryName.equalsIgnoreCase(country1.getCountryName().toString()) && a>provider.getId()){
                    a=provider.getId();
                    serviceProvider=provider;
                    country=country1;
                }
            }
        }
        if(serviceProvider!=null){
            Connection connection=new Connection();
            connection.setUser(user);
            connection.setServiceProvider(serviceProvider);

            String cc=country.getCode()+"."+serviceProvider.getId()+"."+userId;
            user.setMaskedIp(cc);
            user.setConnected(true);
            user.getConnectionList().add(connection);

            serviceProvider.getConnectionList().add(connection);

            userRepository2.save(user);
            serviceProviderRepository2.save(serviceProvider);
        }
        return user;
    }
    @Override
    public User disconnect(int userId) throws Exception {
        User user=userRepository2.findById(userId).get();
        if(user.getConnected()==false) throw new Exception("Already disconnected");
        user.setMaskedIp(null);
        user.setConnected(false);
        userRepository2.save(user);
        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User user=userRepository2.findById(senderId).get();
        User user1=userRepository2.findById(receiverId).get();
        if(user1.getMaskedIp()!=null){
            String str= user1.getMaskedIp();
            String codeCountry=str.substring(0,3); //to get countryNme first 3 digit
            if(codeCountry.equalsIgnoreCase(user.getOriginalCountry().getCode()))
                return user;
            else {
                String cnrty="";
                for (CountryName countryName:AdminServiceImpl.countryMap.values())
                    if(codeCountry.equalsIgnoreCase(countryName.toCode())){
                        cnrty=countryName.name().toString();
                    }
                User user2=connect(senderId,cnrty);
                if(!user2.getConnected()){
                    throw new Exception("Cannot establish communication");
                }
                else return user2;
            }
        }else {
            if(user1.getOriginalCountry().equals(user.getOriginalCountry())){
                return user;
            }
            String countryName=user1.getOriginalCountry().getCountryName().toString();
            User user2=connect(senderId,countryName);
            if(!user2.getConnected()){
                throw new Exception("Cannot establish communication");
            }
            return user2;
        }

    }
}

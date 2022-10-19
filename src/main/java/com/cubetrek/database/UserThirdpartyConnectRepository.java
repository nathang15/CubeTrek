package com.cubetrek.database;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserThirdpartyConnectRepository extends JpaRepository<UserThirdpartyConnect, Long> {

    UserThirdpartyConnect findByGarminUseraccesstoken(String GarminUseraccesstoken);
    UserThirdpartyConnect findByUser(Users user);

    void deleteByUser(Users users);

}
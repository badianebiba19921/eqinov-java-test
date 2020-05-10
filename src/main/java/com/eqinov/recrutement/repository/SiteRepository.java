package com.eqinov.recrutement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eqinov.recrutement.data.Site;

public interface SiteRepository extends JpaRepository<Site, Long> {
}
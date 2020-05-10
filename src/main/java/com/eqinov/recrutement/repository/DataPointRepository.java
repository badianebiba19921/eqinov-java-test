package com.eqinov.recrutement.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eqinov.recrutement.data.DataPoint;
import com.eqinov.recrutement.data.DataPointId;
import com.eqinov.recrutement.data.Site;
import com.eqinov.recrutement.dto.DataPeriod;
import com.eqinov.recrutement.dto.DataYear;

public interface DataPointRepository  extends JpaRepository<DataPoint, DataPointId> {	

	List<DataPoint> findBySite(Site site);

	List<DataPoint> findBySiteAndTimeBetween(Site site, LocalDateTime start, LocalDateTime end);

	DataPoint findTopBySiteOrderByTimeDesc(Site site);

	DataPoint findTopBySiteOrderByTimeAsc(Site site);

	@Query(nativeQuery=true, value="SELECT YEAR(TIME)*1000000 + MONTH(TIME)*10000 + DAY(TIME)*100 + HOUR(TIME) AS PERIOD, AVG(VALUE) AS MEAN FROM DATA_POINT WHERE YEAR(TIME)=:year GROUP BY PERIOD ORDER BY PERIOD")
	public List<DataPeriod> getDataPeriod(@Param("year") Integer currentYear);

	@Query(nativeQuery=true, value="SELECT YEAR(TIME) AS PERIOD, SUM(VALUE) AS SUM FROM DATA_POINT WHERE YEAR(TIME)=:year GROUP BY PERIOD")
	public DataYear getDataYear(@Param("year") Integer currentYear);
	
	@Query(nativeQuery=true, value="SELECT COUNT(*) FROM DATA_POINT WHERE YEAR(TIME)=:year")
	public Integer checkYearPresent(@Param("year") Integer currentYear);
}
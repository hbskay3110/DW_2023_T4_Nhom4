package com.watermelon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.watermelon.model.entity.LottoResultEntity;
import java.util.List;
import java.util.Date;

public interface LottoResultRepository extends JpaRepository<LottoResultEntity, Integer> {
	
	 @Query("SELECT r FROM LottoResultEntity r WHERE r.location = :location AND r.date = (SELECT MAX(rInner.date) FROM LottoResultEntity rInner WHERE rInner.location = :location)")
	    List<LottoResultEntity> findLatestByLocation(@Param("location") String location);


	@Query("SELECT r FROM LottoResultEntity r WHERE r.location = :location AND r.date = :date")
	List<LottoResultEntity> findByLocationAndDate(@Param("location") String location, @Param("date") Date date);
	@Query("SELECT r FROM LottoResultEntity r WHERE r.region = :region AND r.date = (SELECT MAX(rInner.date) FROM LottoResultEntity rInner WHERE rInner.region = :region)")
	public List<LottoResultEntity> findByRegion(@Param("region") String region);

	public List<LottoResultEntity> findByRegionAndDate(String region, Date date);

}

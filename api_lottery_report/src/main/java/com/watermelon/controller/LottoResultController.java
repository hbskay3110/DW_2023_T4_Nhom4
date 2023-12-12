package com.watermelon.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.watermelon.model.entity.LocationResult;
import com.watermelon.model.entity.LottoResultEntity;
import com.watermelon.model.entity.PrizeResult;
import com.watermelon.model.response.ELocation;
import com.watermelon.repository.LottoResultRepository;

@RestController
@RequestMapping("/api/lottery")
@CrossOrigin("*")
public class LottoResultController {
	@Autowired
	private LottoResultRepository lottoResultRepository;

	@GetMapping("/MN")
	public List<LocationResult> getAllLoteryResult() {
		Map<String, LocationResult> locationResults = new HashMap<>();
		List<LottoResultEntity> lottoResultEntities = lottoResultRepository.findAll();
		List<LocationResult> finalResult = getData(locationResults, lottoResultEntities);
		return finalResult;

	}

	@GetMapping("/{location}")
	public List<LocationResult> getResultByLocation(@PathVariable(value = "location") String location) {
		if (location != null) {
			ELocation[] provinces = ELocation.values();
			// Duyệt qua từng hằng số và kiểm tra tên tương ứng với mã
			for (ELocation province : provinces) {
				System.out.println(location.equals(province.getCode()));
				if (location.equals(province.getCode())) {
					location = province.getName();
					break; // Exit the loop once a match is found
				}
			}
			Map<String, LocationResult> locationResults = new HashMap<>();
			List<LottoResultEntity> lottoResultEntities = location.startsWith("Mi") ?lottoResultRepository.findByRegion(location): lottoResultRepository.findLatestByLocation(location);
			List<LocationResult> finalResult = getData(locationResults, lottoResultEntities);

			return finalResult;
		} else {
			// Handle the case where location is null, for example, return an error response
			// You can customize this based on your application's requirements
			return null;
		}
	}

	@GetMapping("/{location}/{date}")
	public List<LocationResult> getResultByLocation(@PathVariable(value = "location") String pathLocation,
			@PathVariable(value = "date") String date) {
		if (pathLocation != null) {
			ELocation[] provinces = ELocation.values();
			// Duyệt qua từng hằng số và kiểm tra tên tương ứng với mã
			for (ELocation province : provinces) {
				if (pathLocation.equals(province.getCode())) {
					pathLocation = province.getName();
					break; // Exit the loop once a match is found
				}
			}

			StringTokenizer tokenizer = new StringTokenizer(date, "-");
			int day = Integer.parseInt(tokenizer.nextToken());
			int month = Integer.parseInt(tokenizer.nextToken());
			int year = Integer.parseInt(tokenizer.nextToken());
			LocalDate localDate = LocalDate.of(year, month, day);
			Date dateTime = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

			Map<String, LocationResult> locationResults = new HashMap<>();
			List<LottoResultEntity> lottoResultEntities = pathLocation.startsWith("Mi") ?
					lottoResultRepository.findByRegionAndDate(pathLocation, dateTime)
					:
					lottoResultRepository.findByLocationAndDate(pathLocation,dateTime);
			List<LocationResult> finalResult = getData(locationResults, lottoResultEntities);

			return finalResult;
		} else {
			// Handle the case where location is null, for example, return an error response
			// You can customize this based on your application's requirements
			return Collections.emptyList(); // Return an empty list for simplicity
		}
	}
	

	public List<LocationResult> getData(Map<String, LocationResult> locationResults,
			List<LottoResultEntity> lottoResultEntities) {
		for (LottoResultEntity entity : lottoResultEntities) {
			String key = entity.getLocation();

			if (!locationResults.containsKey(key)) {
				LocationResult locationResult = new LocationResult(entity.getId(), entity.getLocation(),
						entity.getWeekday(), new SimpleDateFormat("dd-MM-yyyy").format(entity.getDate()),
						entity.getRegion());
				locationResults.put(key, locationResult);
			}

			LocationResult locationResult = locationResults.get(key);
			List<String> result = Arrays.asList(entity.getResult().split("\\s+")).stream()
					.filter(s -> s.matches("\\d+"))
//		            .map(Integer::parseInt)
					.collect(Collectors.toList());

			locationResult.addPrize(entity.getPrize(), result);
		}

		List<LocationResult> finalResult = new ArrayList<>(locationResults.values());

		// Sắp xếp theo giải
		finalResult.forEach(locationResult -> {
			locationResult.setPrizes(locationResult.getPrizes().stream()
					.sorted(Comparator.comparing(PrizeResult::getPrizeName, (s1, s2) -> {
						// Sắp xếp theo thứ tự giải từ Đặc biệt đến Giải tám
						List<String> prizesOrder = locationResult.getLocation().equals(ELocation.MIEN_BAC.getName())?List.of("Giải bảy", "Giải sáu", "Giải tư", "Giải năm",
								"Giải ba", "Giải nhì", "Giải nhất", "Đặc biệt") :List.of("Giải tám", "Giải bảy", "Giải sáu", "Giải tư", "Giải năm",
								"Giải ba", "Giải nhì", "Giải nhất", "Đặc biệt");
						return Integer.compare(prizesOrder.indexOf(s1), prizesOrder.indexOf(s2));
					})).collect(Collectors.toList()));
		});
		return finalResult;
	}

}

package com.eqinov.recrutement.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eqinov.recrutement.data.DataPoint;
import com.eqinov.recrutement.data.Site;
import com.eqinov.recrutement.dto.DataPeriod;
import com.eqinov.recrutement.dto.DataYear;
import com.eqinov.recrutement.repository.DataPointRepository;
import com.eqinov.recrutement.repository.SiteRepository;
import com.eqinov.recrutement.utils.DateUtils;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

/**
 * Controller Spring permettant l'affichage des donn�es dans la seule vue de
 * l'application
 * 
 * @author Guillaume SIMON - EQINOV
 * @since 27 janv. 2020
 *
 */
@Controller
public class WelcomeController {

	@Autowired
	private SiteRepository siteRepository;

	@Autowired
	private DataPointRepository dataPointRepository;

	/**
	 * Point d'entr�e de la vue, page d'accueil de l'application
	 */
	@GetMapping("/")
	public String main(@RequestParam(required=false) String message, Model model) {
		Optional<Site> site = siteRepository.findById(1l);
		if (site.isPresent()) {
			if(message != null) {
				model.addAttribute("message", message);
			}
			Integer maxYear = dataPointRepository.findTopBySiteOrderByTimeDesc(site.get()).getTime().getYear();
			initModel(site.get(), maxYear, model);
		}
		return "welcome";
	}

	/**
	 * Rafraichi le contenu de la page sur changement d'ann�e
	 * 
	 * @param year  l'ann�e
	 * @param model model transportant les donn�es
	 * @return le fragment a retourn�
	 */
	@GetMapping("/view/refresh")
	public String refresh(@RequestParam Integer year, Model model) {
		Optional<Site> site = siteRepository.findById(1l);
		if (site.isPresent()) {
			initModel(site.get(), year, model);
		}
		return "welcome:: result";
	}

	/**
	 * M�thode interne permettant d'ajouter les donn�es du site pour l'ann�e �
	 * afficher
	 * 
	 * @param site        site � afficher
	 * @param currentYear ann�e s�lectionn�e
	 * @param model       model transportant les donn�es
	 */
	private void initModel(Site site, Integer currentYear, Model model) {
		Integer minYear = dataPointRepository.findTopBySiteOrderByTimeAsc(site).getTime().getYear();
		Integer maxYear = dataPointRepository.findTopBySiteOrderByTimeDesc(site).getTime().getYear();
		List<Integer> years = Stream.iterate(minYear, n -> n + 1).limit((maxYear - minYear) + 1l).map(n -> n)
				.collect(Collectors.toList());
		
		model.addAttribute("years", years);
		model.addAttribute("currentYear", currentYear);
		model.addAttribute("site", site);
		Map<String, Double> dataTable = getDataTable(currentYear);
		model.addAttribute("dataTable", dataTable);
		model.addAttribute("dataYear", getDataYeay(currentYear));
	}

	/**
	 * Retourne les points de consommation d'une ann�e au format json pour highstock
	 * 
	 * @param year ann�e
	 * @return
	 */
	@GetMapping(value = "/data/conso", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<double[]> getConso(@RequestParam Integer year) {
		Optional<Site> site = siteRepository.findById(1l);
		List<double[]> result = new ArrayList<>();
		if (site.isPresent()) {
			List<DataPoint> points = dataPointRepository.findBySiteAndTimeBetween(site.get(),
					LocalDate.of(year, 1, 1).atStartOfDay(),
					LocalDate.of(year, 12, 31).atStartOfDay().with(LocalTime.MAX));
			result = points.stream().map(point -> {
				double[] array = new double[2];
				array[0] = DateUtils.secondsFromEpoch(point.getTime()) * 1000l;
				array[1] = point.getValue();
				return array;
			}).collect(Collectors.toList());
		}
		return result;
	}

	/**
	 * Load the evolution of monthly consumption
	 * 
	 * @param currentYear
	 * @return
	 */
	private Map<String, Double> getDataTable(Integer currentYear){

		Map<String, Double> data = new LinkedHashMap<>();
		List<DataPeriod> periods = dataPointRepository.getDataPeriod(currentYear);
		Map<Integer, Double> result = periods.stream().collect(
				Collectors.groupingBy(period -> period.getMonth(), Collectors.summingDouble(DataPeriod::getMean)));
		for(int key=1; key<=12; key++) {
			Double value = new Double(((new BigDecimal(result.get(key).doubleValue())).setScale(2, BigDecimal.ROUND_DOWN)).doubleValue());
			data.put(LocalDate.of(1975, key, 1).getMonth().toString(), value);
		}

		return data;
	}
	
	/**
	 * Load the annual consumption
	 * 
	 * @param currentYear
	 * @return
	 */
	private double getDataYeay(Integer currentYear){

		DataYear dataYear = dataPointRepository.getDataYear(currentYear);
		double sum = ((new BigDecimal(dataYear.getSum())).setScale(2, BigDecimal.ROUND_DOWN)).doubleValue();
		return sum;
	}

	/**
	 * Load data history
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/data/history")
	public String loadDataHistory(RedirectAttributes attributes) {

		try {
			URL url = new URL("http://localhost:2345/api/conso");
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setRequestMethod("GET");
			httpCon.setRequestProperty("Accept", "application/json");
			if (httpCon.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + httpCon.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((httpCon.getInputStream())));
			StringBuffer result = new StringBuffer();  
	        String line = "";  
	        while ((line = br.readLine()) != null) {  
	            result.append(line);  
	        }
	        JSONObject jobject = (JSONObject) JSONValue.parse(result.toString()); 
	        Optional<Site> site = siteRepository.findById(1l);
	        if(site.isPresent()) {
	        	List<DataPoint> consos = new LinkedList<>();
		        JSONArray values = (JSONArray) jobject.get("values");
		        Integer year = LocalDateTime.parse(((JSONObject) values.get(0)).getAsString("date").replace(" ", "T")).getYear();
		        Integer isPresent = dataPointRepository.checkYearPresent(year);
		        if(isPresent.intValue()>0) {
		        	attributes.addFlashAttribute("message", "L'historique des données pour l'année " + year + " est déjà chargée.");
		        }else {
		        	for(int i=0; i<values.size(); i++) {
		        		JSONObject value = (JSONObject) values.get(i);
		        		DataPoint dataPoint = new DataPoint();
		        		dataPoint.setSite(site.get());
		        		dataPoint.setTime(LocalDateTime.parse(value.getAsString("date").replace(" ", "T")));
		        		dataPoint.setValue(Double.parseDouble(value.getAsString("value")));
		        		consos.add(dataPoint);
		        	}
		        	dataPointRepository.saveAll(consos);
		        	attributes.addAttribute("message", "L'historique des consommations pour l'année " + year + " est récupérée avec succès.");
		        }
	        }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "redirect:/";
	}

}
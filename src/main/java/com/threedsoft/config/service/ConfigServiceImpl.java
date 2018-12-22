package com.threedsoft.config.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.core.Ordered;

import com.threedsoft.config.db.ConfigRepository;
import com.threedsoft.config.dto.responses.ConfigDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigServiceImpl implements EnvironmentRepository, Ordered {

	@Autowired
	private ConfigRepository repository;

	@Override
	public Environment findOne(String application, String profile, String label) {

		String propertySourceKey = application + "-" + profile + label == null ? "" : ("-" + label);
		PropertySource propertySource = new PropertySource(application + "-" + profile, getAllConfigForSCC());

		String[] profiles = new String[1];
		profiles[0] = profile;

		Environment env = new Environment(application, profiles, label, null, null);
		env.add(propertySource);

		return env;
	}

	public Map<String, String> getAllConfigForSCC() {
		List<Map<String, Object>> configList = repository.getAllConfig();
		return getProperties(configList);
	}

	public List<ConfigDTO> getAllConfig(String busName, Integer locnNbr) {
		List<ConfigDTO> configResourceDTOList = null;
		configResourceDTOList = repository.getAllConfig(busName, locnNbr);
		return configResourceDTOList;
	}

	private Map<String, String> getProperties(List<Map<String, Object>> dbConfigList) {
		Map<String, String> propertyMap = new HashMap<String, String>();
		for (Map<String, Object> dbconfig : dbConfigList) {
			propertyMap.put(String.valueOf(dbconfig.get("key")), String.valueOf(dbconfig.get("value")));
		}
		return propertyMap;
	}

	@Override
	public int getOrder() {
		return 2;
	}

	@Transactional
	public ConfigDTO overrideConfigForBusName(String busName, ConfigDTO configResource) throws Exception {
		ConfigDTO responseConfig = null;
		if (configResource.getWbcId() == null) {
			// insert
			responseConfig = repository.insertBusConfigValue(configResource.getId(), configResource.getBusOverrideVal(),
					busName, configResource.getComments());

		} else {
			responseConfig = repository.updateBusLocnConfigValue(configResource.getWbcId(),
					configResource.getBusOverrideVal(), configResource.getComments());
		}
		return responseConfig;
	}

	@Transactional
	public ConfigDTO overrideConfigForBusNameAndLocnNbr(String busName, Integer locnNbr, ConfigDTO configResource)
			throws Exception {
		ConfigDTO responseConfig = null;
		if (configResource.getWblcId() == null) {
			// insert
			responseConfig = repository.insertBusLocnConfigValue(configResource.getId(),
					configResource.getBusLocnOverrideVal(), busName, locnNbr, configResource.getComments());
		} else {
			responseConfig = repository.updateBusLocnConfigValue(configResource.getWblcId(),
					configResource.getBusLocnOverrideVal(), configResource.getComments());
		}
		return responseConfig;
	}
}
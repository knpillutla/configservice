package com.threedsoft.config.db;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.threedsoft.config.dto.responses.ConfigDTO;

@Component
public class ConfigRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<Map<String, Object>> getAllConfig() {
		String selectSql = "select concat(module,'.',key) as key, value from wms_config "
				+ "union "
				+ "select concat(wc.module,'.',CONCAT(bus_name, '.', wc.key)) as key , wbc.value from wms_bus_config wbc inner join wms_config wc on wbc.wms_config_id=wc.id "
				+ "union "
				+ "select concat(wc.module,'.',concat(CONCAT(wblc.bus_name, '.', wblc.locn_nbr),'.',wc.key)) as key , wblc.value from wms_bus_locn_config wblc inner join wms_config wc on wblc.wms_config_id=wc.id ";
		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(selectSql);
		return resultList;
	}

	public List<ConfigDTO> getAllConfig(String busName, Integer locnNbr) {
		// get key, def value, bus override if exists, bus locn override if exists. This will help display in the ui
		String selectSql = "select wc.id, (case when wbc is null then -1 else wbc.id end) as wbcId, "
				+ "(case when wblc is null then -1 else wblc.id end) as wblcId, "
				+ "wc.application,wc.profile,wc.module,wc.key,  "
				+ "wc.value as value, "
				+ "(case when wbc is null then 'NOT-SET' else wbc.value end) as busOverrideVal,"
				+ "(case when wblc is null then 'NOT-SET' else wblc.value end) as busLocnOverrideVal, "
				+ "wblc.created_by as userId, wblc.comments "
				+ "from wms_config wc "
				+ "left outer join wms_bus_config wbc on wbc.wms_config_id=wc.id and wbc.bus_name=? "
				+ "left outer join wms_bus_locn_config wblc on wblc.wms_config_id=wc.id and wblc.bus_name=? and wblc.locn_nbr=?";
		List<ConfigDTO> resultList = jdbcTemplate.query(selectSql, new BeanPropertyRowMapper(ConfigDTO.class), busName, busName,
				locnNbr);
		return resultList;
	}

/*	public List<ConfigDTO> getAllConfig(String busName, Integer locnNbr) {
		
		 * String selectSql =
		 * "select wc.id, wc.application,wc.profile,wc.module,wc.key,  (case when wblc is not null then wblc.value when wblc is null and wbc is not null then wbc.value else wc.value end) as value, "
		 * +
		 * "(case when wblc is null then 'N' else 'Y' end) as isBusNameAndLocnOverrideConfig, (case when wbc is null then 'N' else 'Y' end) as isBusNameOverrideConfig,wc.UPDATED_BY as userId "
		 * + "from wms_config wc " +
		 * "left outer join wms_bus_config wbc on wbc.wms_config_id=wc.id and wbc.bus_name=:busName "
		 * +
		 * "left outer join wms_bus_locn_config wblc on wblc.wms_bus_config_id=wbc.id and wblc.locn_nbr=:locnNbr"
		 * ;
		 
		String selectSql = "select wc.id, (case when wblc is null then -1 else wblc.id end) as wblcId, wc.application,wc.profile,wc.module,wc.key,  wc.value, (case when wblc is null then 'NOT-SET' else wblc.value end) as busLocnOverrideVal, wblc.created_by as userId, wblc.comments "
				+ "from wms_config wc "
//				+ "left outer join wms_bus_config wbc on wbc.wms_config_id=wc.id and wbc.bus_name=:busName "
				+ "left outer join wms_bus_locn_config wblc on wblc.wms_config_id=wc.id and wblc.bus_name=? and wblc.locn_nbr=?";
		List<ConfigDTO> resultList = jdbcTemplate.query(selectSql, new BeanPropertyRowMapper(ConfigDTO.class), busName,
				locnNbr);
		return resultList;
	}*/

	public List<ConfigDTO> updateBusConfigValue(Long busConfigId, String value, String comments) throws Exception {
		String updateSql = "update wms_bus_locn_config set value=?, comments=? where id=?";
		int updatedRecords = jdbcTemplate.update(updateSql, value, comments, busConfigId);
		if (updatedRecords > 0) {
			String selectSql = "select wc.id, wbc.id as wbcId, wc.application,wc.profile,wc.module,wc.key,  wc.value, wbc.value as busOverrideVal, wbc.created_by as userId, wbc.comments "
					+ "from wms_config wc "
					+ "inner join wms_bus_config wbc on wbc.wms_config_id=wc.id and wbc.id=?";
			List<ConfigDTO> resultList = jdbcTemplate.query(selectSql, new BeanPropertyRowMapper(ConfigDTO.class),
					busConfigId);
			return resultList;
		}
		throw new Exception("No record updated for busConfigId:" + busConfigId + ",value:" + value);
	}

	public List<ConfigDTO> updateBusLocnConfigValue(Long busLocnConfigId, String value, String comments) throws Exception {
		String updateSql = "update wms_bus_locn_config set value=?, comments=? where id=?";
		int updatedRecords = jdbcTemplate.update(updateSql, value, comments, busLocnConfigId);
		if (updatedRecords > 0) {
			String selectSql = "select wc.id, wblc.id as wblcId, wc.application,wc.profile,wc.module,wc.key,  wc.value, wblc.value as busLocnOverrideVal, wblc.created_by as userId, wblc.comments "
					+ "from wms_config wc "
					+ "inner join wms_bus_locn_config wblc on wblc.wms_config_id=wc.id and wblc.id=?";
			List<ConfigDTO> resultList = jdbcTemplate.query(selectSql, new BeanPropertyRowMapper(ConfigDTO.class),
					busLocnConfigId);
			return resultList;
		}
		throw new Exception("No record updated for busLocnConfigId:" + busLocnConfigId + ",value:" + value);
	}
	
	public List<ConfigDTO> insertBusConfigValue(Long configId, String value, String busName,	String comments) throws Exception {
		String insertSql = "INSERT INTO WMS_BUS_CONFIG(WMS_CONFIG_ID, BUS_NAME, LOCN_NBR, VALUE, COMMENTS) VALUES(?,?,?,?,?)";
		int updatedRecords = jdbcTemplate.update(insertSql, configId, busName, value, comments);
		if (updatedRecords > 0) {
			String selectSql = "select wc.id, wbc.id as wbcId, wc.application,wc.profile,wc.module,wc.key,  wc.value, wbc.value as busOverrideVal, wbc.created_by as userId, wbc.comments "
					+ "from wms_config wc "
					+ "inner join wms_bus_config wbc on wbc.wms_config_id=wc.id and wc.id=? and wbc.bus_name=?";
			List<ConfigDTO> resultList = jdbcTemplate.query(selectSql, new BeanPropertyRowMapper(ConfigDTO.class),
					configId, busName);
			return resultList;
		}
		throw new Exception(
				"No record inserted for configId:" + configId + ",busName:" + busName );
	}

	public List<ConfigDTO> insertBusLocnConfigValue(Long configId, String value, String busName, Integer locnNbr,
			String comments) throws Exception {
		String insertSql = "INSERT INTO WMS_BUS_LOCN_CONFIG(WMS_CONFIG_ID, BUS_NAME, LOCN_NBR, VALUE, COMMENTS) VALUES(?,?,?,?,?)";
		int updatedRecords = jdbcTemplate.update(insertSql, configId, busName, locnNbr, value, comments);
		if (updatedRecords > 0) {
			String selectSql = "select wc.id, wblc.id as wblcId, wc.application,wc.profile,wc.module,wc.key,  wc.value, wblc.value as busLocnOverrideVal, wblc.created_by as userId, wblc.comments "
					+ "from wms_config wc "
					+ "inner join wms_bus_locn_config wblc on wblc.wms_config_id=wc.id and wc.id=? and wblc.bus_name=? and wblc.locn_nbr=?";
			List<ConfigDTO> resultList = jdbcTemplate.query(selectSql, new BeanPropertyRowMapper(ConfigDTO.class),
					configId, busName, locnNbr);
			return resultList;
		}
		throw new Exception(
				"No record inserted for configId:" + configId + ",busName:" + busName + ",locnNbr:" + locnNbr);
	}
}

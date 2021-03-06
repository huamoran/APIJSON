/*Copyright ©2016 TommyLemon(https://github.com/TommyLemon/APIJSON)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package zuo.biao.apijson.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;

import zuo.biao.apijson.JSON;
import zuo.biao.apijson.StringUtil;
import zuo.biao.apijson.Table;

/**config model for query
 * @author Lemon
 */
public class QueryConfig {

	public static final String KEY_COLUMNS = "columns";

	private long id;
	private RequestMethod method;
	private String table;
	private String columns;
	private String values;
	private Map<String, Object> where;
	private int limit;
	private int page;
	private int position;

	public QueryConfig(RequestMethod method) {
		setMethod(method);
	}
	public QueryConfig(RequestMethod method, String table) {
		this(method);
		setTable(table);
	}
	public QueryConfig(RequestMethod method, String table, Map<String, Object> where) {
		this(method, table);
		setWhere(where);
	}
	public QueryConfig(RequestMethod method, String table, String columns, String values) {
		this(method, table);
		setColumns(columns);
		setValues(values);
	}
	public QueryConfig(RequestMethod method, String table, String[] columns, String[][] values) {
		this(method, table);
		setColumns(columns);
		setValues(values);
	}
	public QueryConfig(RequestMethod method, int limit, int page) {
		this(method);
		setLimit(limit);
		setPage(page);
	}

	public RequestMethod getMethod() {
		return method;
	}
	public QueryConfig setMethod(RequestMethod method) {
		if (method == null) {
			method = RequestMethod.GET;
		}
		this.method = method;
		return this;
	}
	public String getTable() {
		return table;
	}
	public QueryConfig setTable(String table) {
		this.table = table;
		return this;
	}
	public String getColumns() {
		return columns;
	}
	public QueryConfig setColumns(String[] columns) {
		return setColumns(StringUtil.getString(columns));
	}
	public QueryConfig setColumns(String columns) {
		this.columns = columns;
		return this;
	}
	private String getColumnsString() {
		switch (method) {
		case POST:
			return StringUtil.isNotEmpty(columns, true) ? "(" + columns + ")" : "";
		default:
			return StringUtil.isNotEmpty(columns, true) ? columns : "*";
		}
	}

	public long getId() {
		return id;
	}
	public QueryConfig setId(long id) {
		this.id = id;
		return this;
	}
	
	public String getValues() {
		return values;
	}
	public String getValuesString() {
		return values;
	}
	public QueryConfig setValues(String[][] values) {
		String s = "";
		if (values != null && values.length > 0) {
			String[] items = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				items[i] = "(" + StringUtil.getString(values[i]) + ")";
			}
			s = StringUtil.getString(items);
		}
		return setValues(s);
	}
	public QueryConfig setValues(String values) {
		this.values = values;
		return this;
	}
	public Map<String, Object> getWhere() {
		return where;
	}
	public QueryConfig setWhere(Map<String, Object> where) {
		this.where = where;
		return this;
	}
	public int getLimit() {
		return limit;
	}
	public QueryConfig setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	public int getPage() {
		return page;
	}
	public QueryConfig setPage(int page) {
		this.page = page;
		return this;
	}
	public int getPosition() {
		return position;
	}
	public QueryConfig setPosition(int position) {
		this.position = position;
		return this;
	}

	/**获取限制数量
	 * @return
	 */
	public String getLimitString() {
		return getLimitString(page, limit);// + 1);
	}
	/**获取限制数量
	 * @param limit
	 * @return
	 */
	public static String getLimitString(int page, int limit) {
		return limit <= 0 ? "" : " limit " + page*limit + ", " + limit;
	}

	/**获取筛选方法
	 * @return
	 */
	public String getWhereString() {
		return getWhereString(where);
	}
	/**获取筛选方法
	 * @param where
	 * @return
	 */
	public static String getWhereString(Map<String, Object> where) {
		Set<String> set = where == null ? null : where.keySet();
		if (set != null && set.size() > 0) {
			String whereString = " where ";
			for (String key : set) {
				//避免筛选到全部	value = key == null ? null : where.get(key);
				if (key == null) {
					continue;
				}
				whereString += (key + "='" + where.get(key) + "' and ");
			}
			if (whereString.endsWith("and ")) {
				whereString = whereString.substring(0, whereString.length() - "and ".length());
			}
			if (whereString.trim().endsWith("where") == false) {
				return whereString;
			}
		}
		return "";
	}
	/**获取筛选方法
	 * @return
	 */
	public String getSetString() {
		return getSetString(where);
	}
	/**获取筛选方法
	 * @param where
	 * @return
	 */
	public static String getSetString(Map<String, Object> where) {
		Set<String> set = where == null ? null : where.keySet();
		if (set != null && set.size() > 0) {
			if (where.containsKey(Table.ID) == false) {
				return "";
			}
			String setString = " set ";
			for (String key : set) {
				//避免筛选到全部	value = key == null ? null : where.get(key);
				if (key == null || Table.ID.equals(key)) {
					continue;
				}
				setString += (key + "='" + where.get(key) + "' ,");
			}
			if (setString.endsWith(",")) {
				setString = setString.substring(0, setString.length() - 1);
			}
			if (setString.trim().endsWith("set") == false) {
				return setString + " where " + Table.ID + "='" + where.get(Table.ID) + "' ";
			}
		}
		return "";
	}


	/**获取查询配置
	 * @param table
	 * @param request
	 * @return
	 */
	public static synchronized QueryConfig getQueryConfig(RequestMethod method, String table, JSONObject request) {
		QueryConfig config = new QueryConfig(method, table);

		Set<String> set = request == null ? null : request.keySet();
		if (set != null) {
			String columns = request.getString(KEY_COLUMNS);
			if (method == RequestMethod.POST) {
				config.setColumns(StringUtil.getString(set.toArray(new String[]{})));
				String valuesString = "";
				Collection<Object> valueCollection = request.values();
				Object[] values = valueCollection == null || valueCollection.isEmpty() ? null : valueCollection.toArray(new String[]{});
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						valuesString += ((i > 0 ? "," : "") + "'" + values[i] + "'");
					}
				}
				config.setValues("(" + valuesString + ")");
			} else {
				request.remove(KEY_COLUMNS);

				Map<String, Object> transferredRequest = new HashMap<String, Object>();
				for (String key : set) {
					if (JSON.parseObject(request.getString(key)) == null) {//非key-value
						transferredRequest.put(key, request.get(key));
					}
				}
				config.setWhere(transferredRequest);
			}


			if (StringUtil.isNotEmpty(columns, true)) {
				config.setColumns(columns);
			}
		}

		return config.setId(request.getLongValue(Table.ID));
	}

	/**
	 * @return
	 */
	public String getSQL() {
		return getSQL(this);
	}
	/**
	 * @param config
	 * @return
	 */
	public static String getSQL(QueryConfig config) {
		if (config == null) {
			System.out.println("QueryConfig: getSQL  config == null >> return null;");
			return null;
		}
		if (config.getMethod() == null) {
			config.setMethod(RequestMethod.GET);
		}
		switch (config.getMethod()) {
		case POST:
			return "insert into " + config.getTable() + config.getColumnsString() + " values" + config.getValuesString();
		case PUT:
			return "update " + config.getTable() + config.getSetString();
		case DELETE:
			return "delete from " + config.getTable() + config.getWhereString();
		default:
			return "select "+ config.getColumnsString() + " from " + config.getTable()
			+ config.getWhereString() + config.getLimitString();
		}
	}


}

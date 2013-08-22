/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2013 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of JasperReports.
 *
 * JasperReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JasperReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JasperReports. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jasperreports.components.headertoolbar.json;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import net.sf.jasperreports.components.headertoolbar.HeaderToolbarElement;
import net.sf.jasperreports.components.headertoolbar.HeaderToolbarElementUtils;
import net.sf.jasperreports.components.headertoolbar.actions.ConditionalFormattingCommand;
import net.sf.jasperreports.components.headertoolbar.actions.ConditionalFormattingData;
import net.sf.jasperreports.components.headertoolbar.actions.EditColumnHeaderData;
import net.sf.jasperreports.components.headertoolbar.actions.EditColumnValueData;
import net.sf.jasperreports.components.headertoolbar.actions.FilterAction;
import net.sf.jasperreports.components.headertoolbar.actions.FormatCondition;
import net.sf.jasperreports.components.headertoolbar.actions.SortAction;
import net.sf.jasperreports.components.sort.FieldFilter;
import net.sf.jasperreports.components.sort.FilterTypeBooleanOperatorsEnum;
import net.sf.jasperreports.components.sort.FilterTypeDateOperatorsEnum;
import net.sf.jasperreports.components.sort.FilterTypeNumericOperatorsEnum;
import net.sf.jasperreports.components.sort.FilterTypeTextOperatorsEnum;
import net.sf.jasperreports.components.sort.FilterTypesEnum;
import net.sf.jasperreports.components.sort.actions.FilterCommand;
import net.sf.jasperreports.components.sort.actions.FilterData;
import net.sf.jasperreports.components.sort.actions.SortData;
import net.sf.jasperreports.components.table.BaseColumn;
import net.sf.jasperreports.components.table.ColumnGroup;
import net.sf.jasperreports.components.table.GroupCell;
import net.sf.jasperreports.components.table.StandardColumn;
import net.sf.jasperreports.components.table.StandardTable;
import net.sf.jasperreports.components.table.util.TableUtil;
import net.sf.jasperreports.engine.DatasetFilter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRIdentifiable;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.base.JRBasePrintHyperlink;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignDatasetRun;
import net.sf.jasperreports.engine.design.JRDesignTextElement;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.GenericElementJsonHandler;
import net.sf.jasperreports.engine.export.JsonExporterContext;
import net.sf.jasperreports.engine.fonts.FontUtil;
import net.sf.jasperreports.engine.type.JREnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.SortFieldTypeEnum;
import net.sf.jasperreports.engine.util.JRColorUtil;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.engine.util.MessageProvider;
import net.sf.jasperreports.engine.util.MessageUtil;
import net.sf.jasperreports.repo.JasperDesignCache;
import net.sf.jasperreports.web.WebReportContext;
import net.sf.jasperreports.web.commands.CommandTarget;
import net.sf.jasperreports.web.util.JacksonUtil;
import net.sf.jasperreports.web.util.ReportInteractionHyperlinkProducer;
import net.sf.jasperreports.web.util.VelocityUtil;
import net.sf.jasperreports.web.util.WebUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id:ChartThemesUtilities.java 2595 2009-02-10 17:56:51Z teodord $
 */
public class HeaderToolbarElementJsonHandler implements GenericElementJsonHandler
{
	private static final String DEFAULT_PATTERNS_BUNDLE = "net.sf.jasperreports.components.messages";
	private static final String DEFAULT_DATE_PATTERN_KEY = "net.sf.jasperreports.components.date.pattern";
	private static final String DEFAULT_TIME_PATTERN_KEY = "net.sf.jasperreports.components.time.pattern";
	private static final String DEFAULT_NUMBER_PATTERN_KEY = "net.sf.jasperreports.components.number.pattern";
	private static final String DEFAULT_CALENDAR_DATE_PATTERN_KEY = "net.sf.jasperreports.components.calendar.date.pattern";
	private static final String DEFAULT_CALENDAR_DATE_TIME_PATTERN_KEY = "net.sf.jasperreports.components.calendar.date.time.pattern";
	private static final String DATE_PATTERN_BUNDLE = DEFAULT_DATE_PATTERN_KEY + ".bundle";
	private static final String DATE_PATTERN_KEY = DEFAULT_DATE_PATTERN_KEY + ".key";
	private static final String TIME_PATTERN_BUNDLE = DEFAULT_TIME_PATTERN_KEY + ".bundle";
	private static final String TIME_PATTERN_KEY = DEFAULT_TIME_PATTERN_KEY + ".key";
	private static final String NUMBER_PATTERN_BUNDLE = DEFAULT_NUMBER_PATTERN_KEY + ".bundle";
	private static final String NUMBER_PATTERN_KEY = DEFAULT_NUMBER_PATTERN_KEY + ".key";
	private static final String CALENDAR_DATE_PATTERN_BUNDLE = DEFAULT_CALENDAR_DATE_PATTERN_KEY + ".bundle";
	private static final String CALENDAR_DATE_PATTERN_KEY = DEFAULT_CALENDAR_DATE_PATTERN_KEY + ".key";
	private static final String CALENDAR_DATE_TIME_PATTERN_KEY = DEFAULT_CALENDAR_DATE_TIME_PATTERN_KEY + ".key";
	
	private static final String RESOURCE_JIVE_COLUMN_JS = "net/sf/jasperreports/components/headertoolbar/htmlv2/resources/require/jive.interactive.column.js";

	private static final String RESOURCE_HEADERTOOLBAR_CSS = "net/sf/jasperreports/components/headertoolbar/resources/jive.vm.css";

	private static final String HEADER_TOOLBAR_ELEMENT_JSON_TEMPLATE = "net/sf/jasperreports/components/headertoolbar/json/resources/HeaderToolbarElementJsonTemplate.vm";
	
	private static final String PARAM_GENERATED_TEMPLATE_PREFIX = "net.sf.jasperreports.headertoolbar.";
	
	private static final List<String> datePatterns = new ArrayList<String>(); 
	private static final List<String> timePatterns = new ArrayList<String>(); 
	private static final Map<String, String> numberPatternsMap = new LinkedHashMap<String, String>(); 
	
	static {
		// date patterns
		datePatterns.add("dd/MM/yyyy");
		datePatterns.add("MM/dd/yyyy");
		datePatterns.add("yyyy/MM/dd");
		datePatterns.add("EEEEE dd MMMMM yyyy");
		datePatterns.add("MMMMM dd. yyyy");
		datePatterns.add("dd/MM");
		datePatterns.add("dd/MM/yy");
		datePatterns.add("dd-MMM");
		datePatterns.add("dd-MMM-yy");
		datePatterns.add("MMM-yy");
		datePatterns.add("MMMMM-yy");
		datePatterns.add("dd/MM/yyyy h.mm a");
		datePatterns.add("dd/MM/yyyy HH.mm.ss");
		datePatterns.add("MMM");
		datePatterns.add("d/M/yyyy");
		datePatterns.add("dd-MMM-yyyy");
		datePatterns.add("yyyy.MM.dd G 'at' HH:mm:ss z");
		datePatterns.add("EEE. MMM d. ''yy");
		datePatterns.add("yyyy.MMMMM.dd GGG hh:mm aaa");
		datePatterns.add("EEE. d MMM yyyy HH:mm:ss Z");
		datePatterns.add("yyMMddHHmmssZ");
		
		timePatterns.add("hh:mm aaa");
		timePatterns.add("hh:mm:ss aaa");
		timePatterns.add("HH:mm");
		timePatterns.add("HH:mm:ss");

		numberPatternsMap.put("###0;-###0", "-1234");
		numberPatternsMap.put("###0;###0-", "1234-");
		numberPatternsMap.put("###0;(###0)", "(1234)");
		numberPatternsMap.put("###0;(-###0)", "(-1234)");
		numberPatternsMap.put("###0;(###0-)", "(1234-)");
	}
	
	private static class CustomJRExporterParameter extends JRExporterParameter{

		protected CustomJRExporterParameter(String name) {
			super(name);
		}
	}
	
	private static final CustomJRExporterParameter param = new HeaderToolbarElementJsonHandler.CustomJRExporterParameter("exporter_first_attempt");

	public String getJsonFragment(JsonExporterContext context, JRGenericPrintElement element)
	{
		boolean templateAlreadyLoaded = false;

		String htmlFragment = null;
		String tableUUID = element.getPropertiesMap().getProperty(HeaderToolbarElement.PROPERTY_TABLE_UUID);
		ReportContext reportContext = context.getExporter().getReportContext();
		if (reportContext != null && tableUUID != null)//FIXMEJIVE
		{
			String appContext = (String)reportContext.getParameterValue(WebReportContext.APPLICATION_CONTEXT_PATH);
			String columnUuid = element.getPropertiesMap().getProperty(HeaderToolbarElement.PROPERTY_COLUMN_UUID);
			String fieldOrVariableName = element.getPropertiesMap().getProperty(HeaderToolbarElement.PROPERTY_COLUMN_FIELD_OR_VARIABLE_NAME);
			String columnLabel = (String)element.getParameterValue(HeaderToolbarElement.PARAMETER_COLUMN_LABEL);
			if (columnLabel == null) {
				columnLabel = "";
			}
			int columnIndex = Integer.parseInt(element.getPropertiesMap().getProperty(HeaderToolbarElement.PROPERTY_COLUMN_INDEX));
			
			Map<String, Object> contextMap = new HashMap<String, Object>();
			contextMap.put("JRStringUtil", JRStringUtil.class);
			contextMap.put("tableUUID", tableUUID);
			
			JasperReportsContext jrContext = context.getJasperReportsContext();
			WebUtil webUtil = WebUtil.getInstance(jrContext);
			String webResourcesBasePath = webUtil.getResourcesBasePath();
			
			Locale locale = (Locale) reportContext.getParameterValue(JRParameter.REPORT_LOCALE);
			
			if (locale == null) {
				locale = Locale.getDefault();
			}
			
			if (reportContext.getParameterValue(PARAM_GENERATED_TEMPLATE_PREFIX) != null) {
				templateAlreadyLoaded = true;
			} else {
				reportContext.setParameterValue(PARAM_GENERATED_TEMPLATE_PREFIX, true);
				
				contextMap.put("actionBaseUrl", getActionBaseUrl(context));
				contextMap.put("actionBaseData", getActionBaseJsonData(context));
				contextMap.put("jasperreports_tableHeaderToolbar_css", webUtil.getResourcePath(webResourcesBasePath, HeaderToolbarElementJsonHandler.RESOURCE_HEADERTOOLBAR_CSS, true));
				contextMap.put("jiveColumnScript", appContext + webUtil.getResourcePath(webResourcesBasePath, HeaderToolbarElementJsonHandler.RESOURCE_JIVE_COLUMN_JS));
				contextMap.put("msgProvider", MessageUtil.getInstance(jrContext).getLocalizedMessageProvider("net.sf.jasperreports.components.headertoolbar.messages", locale));
			}
			
			if (!(context.getExportParameters().containsKey(param) 
					&& tableUUID.equals(context.getExportParameters().get(param)))) {
				Map<String, ColumnInfo> columnNames = getAllColumnNames(element, jrContext, contextMap);
				List<Map<String, Object>> columnGroupsData = getColumnGroupsData(jrContext, reportContext, tableUUID);
				// column names are normally set on the first column, but check if we got them
				if (!columnNames.isEmpty()) {
					context.getExportParameters().put(param, tableUUID);

					contextMap.put("allColumnNames", JacksonUtil.getInstance(jrContext).getJsonString(columnNames));
					contextMap.put("allColumnGroupsData", JacksonUtil.getInstance(jrContext).getJsonString(columnGroupsData));
					contextMap.put("numberPatterns", JacksonUtil.getInstance(jrContext).getJsonString(getNumberPatterns(numberPatternsMap)));
					contextMap.put("datePatterns", JacksonUtil.getInstance(jrContext).getJsonString(getDatePatterns(datePatterns, locale)));
					contextMap.put("timePatterns", JacksonUtil.getInstance(jrContext).getJsonString(getDatePatterns(timePatterns, locale)));
					contextMap.put("exporterFirstAttempt", true);
				}
			}

			contextMap.put("templateAlreadyLoaded", templateAlreadyLoaded);

			Boolean canSort = Boolean.parseBoolean(element.getPropertiesMap().getProperty(HeaderToolbarElement.PROPERTY_CAN_SORT));
			Boolean canFilter = Boolean.parseBoolean(element.getPropertiesMap().getProperty(HeaderToolbarElement.PROPERTY_CAN_FILTER));
			Boolean canFormatConditionally = Boolean.parseBoolean(element.getPropertiesMap().getProperty(HeaderToolbarElement.PROPERTY_CAN_FORMAT_CONDITIONALLY));

			if (element.getModeValue() == ModeEnum.OPAQUE)
			{
				contextMap.put("backgroundColor", JRColorUtil.getColorHexa(element.getBackcolor()));
			}

			contextMap.put("columnUuid", columnUuid);
			contextMap.put("columnLabel", columnLabel);
			contextMap.put("columnIndex", columnIndex);
			contextMap.put("canSort", canSort);
			contextMap.put("canFilter", canFilter);
			contextMap.put("canFormatConditionally", canFormatConditionally);

			contextMap.put("fontExtensionsFontNames", getFontExtensionsFontNames(jrContext));
			contextMap.put("systemFontNames", getSystemFontNames(jrContext));

			setColumnHeaderData(columnLabel, columnIndex, tableUUID, contextMap, jrContext, reportContext);
			setColumnValueData(columnIndex, tableUUID, contextMap, jrContext, reportContext);

			String columnName = element.getPropertiesMap().getProperty(HeaderToolbarElement.PROPERTY_COLUMN_NAME);
			String columnType = element.getPropertiesMap().getProperty(HeaderToolbarElement.PROPERTY_COLUMN_TYPE);
			FilterTypesEnum filterType = FilterTypesEnum.getByName(element.getPropertiesMap().getProperty(HeaderToolbarElement.PROPERTY_FILTER_TYPE));
			String filterPattern = "";
			String calendarPattern = null;
			String calendarTimePattern = null;

			Map<String, String> translatedOperators = null;
			if (filterType != null) {
				switch (filterType) {
				case NUMERIC:
					translatedOperators = getTranslatedOperators(jrContext, FilterTypeNumericOperatorsEnum.class.getName(), FilterTypeNumericOperatorsEnum.values(), locale);
					String numberPatternBundleName = JRPropertiesUtil.getInstance(jrContext).getProperty(NUMBER_PATTERN_BUNDLE);
					if (numberPatternBundleName == null)
					{
						numberPatternBundleName = DEFAULT_PATTERNS_BUNDLE;
					}
					String numberPatternKey = JRPropertiesUtil.getInstance(jrContext).getProperty(NUMBER_PATTERN_KEY);
					if (numberPatternKey == null)
					{
						numberPatternKey = DEFAULT_NUMBER_PATTERN_KEY;
					}
					filterPattern = getBundleMessage(numberPatternKey, jrContext, numberPatternBundleName, locale);
					
					break;
				case DATE:
					translatedOperators = getTranslatedOperators(jrContext, FilterTypeDateOperatorsEnum.class.getName(), FilterTypeDateOperatorsEnum.values(), locale);
					String datePatternBundleName = JRPropertiesUtil.getInstance(jrContext).getProperty(DATE_PATTERN_BUNDLE);
					if (datePatternBundleName == null)
					{
						datePatternBundleName = DEFAULT_PATTERNS_BUNDLE;
					}
					String datePatternKey = JRPropertiesUtil.getInstance(jrContext).getProperty(DATE_PATTERN_KEY);
					if (datePatternKey == null)
					{
						datePatternKey = DEFAULT_DATE_PATTERN_KEY;
					}
					filterPattern = getBundleMessage(datePatternKey, jrContext, datePatternBundleName, locale);
					
					String calendarDatePatternBundleName = JRPropertiesUtil.getInstance(jrContext).getProperty(CALENDAR_DATE_PATTERN_BUNDLE);
					if (calendarDatePatternBundleName == null)
					{
						calendarDatePatternBundleName = DEFAULT_PATTERNS_BUNDLE;
					}
					String calendarDatePatternKey = JRPropertiesUtil.getInstance(jrContext).getProperty(CALENDAR_DATE_PATTERN_KEY);
					if (calendarDatePatternKey == null)
					{
						calendarDatePatternKey = DEFAULT_CALENDAR_DATE_PATTERN_KEY;
					}
					calendarPattern = getBundleMessage(calendarDatePatternKey, jrContext, calendarDatePatternBundleName, locale);

					String calendarDateTimePatternKey = JRPropertiesUtil.getInstance(jrContext).getProperty(CALENDAR_DATE_TIME_PATTERN_KEY);
					if (calendarDateTimePatternKey == null)
					{
						calendarDateTimePatternKey = DEFAULT_CALENDAR_DATE_TIME_PATTERN_KEY;
					}
					calendarTimePattern = getBundleMessage(calendarDateTimePatternKey, jrContext, calendarDatePatternBundleName, locale);
					break;
				case TIME:
					translatedOperators = getTranslatedOperators(jrContext, FilterTypeDateOperatorsEnum.class.getName(), FilterTypeDateOperatorsEnum.values(), locale);
					String timePatternBundleName = JRPropertiesUtil.getInstance(jrContext).getProperty(TIME_PATTERN_BUNDLE);
					if (timePatternBundleName == null)
					{
						timePatternBundleName = DEFAULT_PATTERNS_BUNDLE;
					}
					
					String timePatternKey = JRPropertiesUtil.getInstance(jrContext).getProperty(TIME_PATTERN_KEY);
					if (timePatternKey == null)
					{
						timePatternKey = DEFAULT_TIME_PATTERN_KEY;
					}
					filterPattern = getBundleMessage(timePatternKey, jrContext, timePatternBundleName, locale);
					
					String calendarTimePatternKey = JRPropertiesUtil.getInstance(jrContext).getProperty(CALENDAR_DATE_TIME_PATTERN_KEY);
					if (calendarTimePatternKey == null)
					{
						calendarTimePatternKey = DEFAULT_CALENDAR_DATE_TIME_PATTERN_KEY;
					}
					calendarTimePattern = getBundleMessage(calendarTimePatternKey, jrContext, timePatternBundleName, locale);
					break;
				case TEXT:
					translatedOperators = getTranslatedOperators(jrContext, FilterTypeTextOperatorsEnum.class.getName(), FilterTypeTextOperatorsEnum.values(), locale);
					break;
				case BOOLEAN:
					translatedOperators = getTranslatedOperators(jrContext, FilterTypeBooleanOperatorsEnum.class.getName(), FilterTypeBooleanOperatorsEnum.values(), locale);
					break;
				}
			}
			
			if (canFilter) {
				// existing filters
				String filterValueStart = "";
				String filterValueEnd = "";
				String filterTypeOperatorValue = "";
				List<DatasetFilter> fieldFilters = getExistingFiltersForField(jrContext, reportContext, tableUUID, columnName);
				
				if (fieldFilters.size() > 0) {
					FieldFilter ff = (FieldFilter)fieldFilters.get(0);
					if (ff.getFilterValueStart() != null) {
						filterValueStart = ff.getFilterValueStart();
					}
					if (ff.getFilterValueEnd() != null) {
						filterValueEnd = ff.getFilterValueEnd();
					}
					filterTypeOperatorValue = ff.getFilterTypeOperator();
				}

				contextMap.put("dataType", filterType.getName());

				FilterData filterData = new FilterData();
				filterData.setTableUuid(tableUUID);
				filterData.setFieldName(columnName);
				filterData.setFilterType(filterType.getName());
				filterData.setFilterPattern(filterPattern);
				filterData.setCalendarPattern(calendarPattern);
				filterData.setCalendarTimePattern(calendarTimePattern);
				if (FilterTypesEnum.TEXT.getName().equals(filterType.getName())) {
					filterData.setFieldValueStart(JRStringUtil.htmlEncode(filterValueStart));
				} else {
					filterData.setFieldValueStart(filterValueStart);
				}
				filterData.setFieldValueEnd(filterValueEnd);
				filterData.setFilterTypeOperator(filterTypeOperatorValue);
				filterData.setIsField(SortFieldTypeEnum.FIELD.equals(SortFieldTypeEnum.getByName(columnType)));
				
				contextMap.put("filterData", JacksonUtil.getInstance(jrContext).getJsonString(filterData));
				contextMap.put("filterTypeValuesMap", translatedOperators);
				contextMap.put("filterTypeOperatorValue", filterTypeOperatorValue);
				contextMap.put("filterTableUuid", tableUUID);
				contextMap.put("filterColumnNameLabel", columnLabel);
			}
			
			if (canSort) {
				SortData sortAscData = new SortData(tableUUID, columnName, columnType, HeaderToolbarElement.SORT_ORDER_ASC);
				SortData sortDescData = new SortData(tableUUID, columnName, columnType, HeaderToolbarElement.SORT_ORDER_DESC);
				String sortField = getCurrentSortField(jrContext, reportContext, tableUUID, columnName, columnType);
				if (sortField != null) 
				{
					String[] sortActionData = HeaderToolbarElementUtils.extractColumnInfo(sortField);
					
					boolean isAscending = HeaderToolbarElement.SORT_ORDER_ASC.equals(sortActionData[2]);
					if (isAscending) {
						sortAscData.setSortOrder(HeaderToolbarElement.SORT_ORDER_NONE);
					} else {
						sortDescData.setSortOrder(HeaderToolbarElement.SORT_ORDER_NONE);
					}
				}
				contextMap.put("sortAscData", JacksonUtil.getInstance(jrContext).getJsonString(sortAscData));
				contextMap.put("sortDescData", JacksonUtil.getInstance(jrContext).getJsonString(sortDescData));
			}
			
			if (canFormatConditionally) {
				ConditionalFormattingData cfData = getExistingConditionalFormattingDataForField(jrContext, reportContext, tableUUID, columnName, columnIndex);
				cfData.setTableUuid(tableUUID);
				cfData.setConditionType(filterType.getName());
				cfData.setCalendarPattern(calendarPattern);
				cfData.setCalendarTimePattern(calendarTimePattern);
				cfData.setConditionPattern(filterPattern);
				cfData.setColumnType(columnType);
				cfData.setFieldOrVariableName(fieldOrVariableName);
				contextMap.put("conditionalFormattingData", JacksonUtil.getInstance(jrContext).getJsonString(cfData));
			}
			
			htmlFragment = VelocityUtil.processTemplate(HeaderToolbarElementJsonHandler.HEADER_TOOLBAR_ELEMENT_JSON_TEMPLATE, contextMap);
		}
		
		return htmlFragment;
	}
	
	private List<HashMap<String, String>> getDatePatterns(List<String> datePatterns, Locale locale) {
        List<HashMap<String, String>> formatPatterns = new ArrayList<HashMap<String, String>>();

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", locale);
		Date today = new Date();
        HashMap<String, String> keys;

		for(String datePattern: datePatterns) {
            keys = new HashMap<String, String>();
			sdf.applyPattern(datePattern);
            keys.put("key", datePattern);
            keys.put("val", sdf.format(today));
			formatPatterns.add(keys);
		}

        return formatPatterns;
	}
	private List<HashMap<String, String>> getNumberPatterns(Map<String, String> numberPatternsMap) {
        List<HashMap<String, String>> formatPatterns = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> keys;

        for(Map.Entry<String, String> entry: numberPatternsMap.entrySet()) {
            keys = new HashMap<String, String>();
            keys.put("key", entry.getKey());
            keys.put("val", entry.getValue());
			formatPatterns.add(keys);
        }

        return formatPatterns;
	}

	private String getActionBaseUrl(JsonExporterContext context) {
		JRBasePrintHyperlink hyperlink = new JRBasePrintHyperlink();
		hyperlink.setLinkType(ReportInteractionHyperlinkProducer.HYPERLINK_TYPE_REPORT_INTERACTION);
//		return context.getHyperlinkURL(hyperlink);
		return "FIXME HeaderToolbarElementJsonHandler.getActionBaseUrl";
	}

	private String getActionBaseJsonData(JsonExporterContext context) {
		ReportContext reportContext = context.getExporter().getReportContext();
		Map<String, Object> actionParams = new HashMap<String, Object>();
		actionParams.put(WebReportContext.REQUEST_PARAMETER_REPORT_CONTEXT_ID, reportContext.getId());
		actionParams.put(WebUtil.REQUEST_PARAMETER_RUN_REPORT, true);
		
//		return JacksonUtil.getInstance(context.getJasperReportsContext()).getEscapedJsonString(actionParams);
		return JacksonUtil.getInstance(context.getJasperReportsContext()).getJsonString(actionParams);
	}

	private String getCurrentSortField(
		JasperReportsContext jasperReportsContext,
		ReportContext reportContext, 
		String uuid, 
		String sortColumnName, 
		String sortColumnType
		) 
	{
		JasperDesignCache cache = JasperDesignCache.getInstance(jasperReportsContext, reportContext);
		SortAction action = new SortAction();
		action.init(jasperReportsContext, reportContext);
		CommandTarget target = action.getCommandTarget(UUID.fromString(uuid));
		if (target != null)
		{
			JRIdentifiable identifiable = target.getIdentifiable();
			JRDesignComponentElement componentElement = identifiable instanceof JRDesignComponentElement ? (JRDesignComponentElement)identifiable : null;
			StandardTable table = componentElement == null ? null : (StandardTable)componentElement.getComponent();
			
			JRDesignDatasetRun datasetRun = (JRDesignDatasetRun)table.getDatasetRun();
			
			String datasetName = datasetRun.getDatasetName();
			
			JasperDesign jasperDesign = cache.getJasperDesign(target.getUri());//FIXMEJIVE getJasperReport not design
			JRDesignDataset dataset = (JRDesignDataset)jasperDesign.getDatasetMap().get(datasetName);
			
			List<JRSortField> existingFields =  dataset.getSortFieldsList();
			String sortField = null;
	
			if (existingFields != null && existingFields.size() > 0) {
				for (JRSortField field: existingFields) {
					if (field.getName().equals(sortColumnName) && field.getType().getName().equals(sortColumnType)) {
						sortField = sortColumnName + HeaderToolbarElement.SORT_COLUMN_TOKEN_SEPARATOR + sortColumnType + HeaderToolbarElement.SORT_COLUMN_TOKEN_SEPARATOR;
						switch (field.getOrderValue()) {
							case ASCENDING:
								sortField += HeaderToolbarElement.SORT_ORDER_ASC;
								break;
							case DESCENDING:
								sortField += HeaderToolbarElement.SORT_ORDER_DESC;
								break;
						}
						break;
					}
				}
			}
		
			return sortField;
		}
		
		return null;
	}
	
	public boolean toExport(JRGenericPrintElement element) {
		return true;
	}
	
	private Map<String, String> getTranslatedOperators(
		JasperReportsContext jasperReportsContext, 
		String bundleName, 
		JREnum[] operators, 
		Locale locale
		) //FIXMEJIVE make utility method for translating enums
	{
		Map<String, String> result = new LinkedHashMap<String, String>();
		MessageProvider messageProvider = MessageUtil.getInstance(jasperReportsContext).getMessageProvider(bundleName);
		
		for (JREnum operator: operators) 
		{
			String key = bundleName + "." + ((Enum<?>)operator).name();
			result.put(((Enum<?>)operator).name(), messageProvider.getMessage(key, null, locale));
		}
		
		return result;
	}
	
	private List<DatasetFilter> getExistingFiltersForField(
		JasperReportsContext jasperReportsContext, 
		ReportContext reportContext, 
		String uuid, 
		String filterFieldName
		) 
	{
		JasperDesignCache cache = JasperDesignCache.getInstance(jasperReportsContext, reportContext);
		FilterAction action = new FilterAction();
		action.init(jasperReportsContext, reportContext);
		CommandTarget target = action.getCommandTarget(UUID.fromString(uuid));
		List<DatasetFilter> result = new ArrayList<DatasetFilter>();
		if (target != null)
		{
			JRIdentifiable identifiable = target.getIdentifiable();
			JRDesignComponentElement componentElement = identifiable instanceof JRDesignComponentElement ? (JRDesignComponentElement)identifiable : null;
			StandardTable table = componentElement == null ? null : (StandardTable)componentElement.getComponent();
			
			JRDesignDatasetRun datasetRun = (JRDesignDatasetRun)table.getDatasetRun();
			
			String datasetName = datasetRun.getDatasetName();
			
			JasperDesign jasperDesign = cache.getJasperDesign(target.getUri());//FIXMEJIVE getJasperReport not design
			JRDesignDataset dataset = (JRDesignDataset)jasperDesign.getDatasetMap().get(datasetName);
			
			// get existing filter as JSON string
			String serializedFilters = "[]";
			JRPropertiesMap propertiesMap = dataset.getPropertiesMap();
			if (propertiesMap.getProperty(FilterCommand.DATASET_FILTER_PROPERTY) != null) {
				serializedFilters = propertiesMap.getProperty(FilterCommand.DATASET_FILTER_PROPERTY);
			}
			
			List<? extends DatasetFilter> existingFilters = JacksonUtil.getInstance(jasperReportsContext).loadList(serializedFilters, FieldFilter.class);
			if (existingFilters.size() > 0) {
				for (DatasetFilter filter: existingFilters) {
					if (((FieldFilter)filter).getField().equals(filterFieldName)) {
						result.add(filter);
						break;
					}
				}
			}
		}
		
		return result;		
	}

	private ConditionalFormattingData getExistingConditionalFormattingDataForField(
			JasperReportsContext jasperReportsContext, 
			ReportContext reportContext, 
			String tableUuid, 
			String fieldName,
			Integer columnIndex
			)
	{
		FilterAction action = new FilterAction();
		action.init(jasperReportsContext, reportContext);
		CommandTarget target = action.getCommandTarget(UUID.fromString(tableUuid));
		ConditionalFormattingData result = new ConditionalFormattingData();
		if (target != null)
		{
			JRIdentifiable identifiable = target.getIdentifiable();
			JRDesignComponentElement componentElement = identifiable instanceof JRDesignComponentElement ? (JRDesignComponentElement)identifiable : null;
			StandardTable table = componentElement == null ? null : (StandardTable)componentElement.getComponent();
			
			List<BaseColumn> tableColumns = TableUtil.getAllColumns(table);
			
			if (columnIndex != null) {
				StandardColumn column = (StandardColumn) tableColumns.get(columnIndex);
				
				JRDesignTextField textElement = (JRDesignTextField)TableUtil.getColumnDetailTextElement(column);
				
				if (textElement != null) {
					JRPropertiesMap propertiesMap = textElement.getPropertiesMap();
					if (propertiesMap.containsProperty(ConditionalFormattingCommand.COLUMN_CONDITIONAL_FORMATTING_PROPERTY) && propertiesMap.getProperty(ConditionalFormattingCommand.COLUMN_CONDITIONAL_FORMATTING_PROPERTY) != null) {
						result = JacksonUtil.getInstance(jasperReportsContext).loadObject(propertiesMap.getProperty(ConditionalFormattingCommand.COLUMN_CONDITIONAL_FORMATTING_PROPERTY), ConditionalFormattingData.class);
						
						// html encode the conditions for text based columns
						if (FilterTypesEnum.TEXT.getName().equals(result.getConditionType())) {
							for (FormatCondition fc: result.getConditions()) {
								fc.setConditionStart(JRStringUtil.htmlEncode(fc.getConditionStart()));
							}
						}
					}
				}
			}
		}
		
		return result;
	}

	private void setColumnHeaderData(String sortColumnLabel, Integer columnIndex, String tableUuid, Map<String, Object> contextMap, JasperReportsContext jasperReportsContext, ReportContext reportContext) {
		FilterAction action = new FilterAction();
		action.init(jasperReportsContext, reportContext);
		CommandTarget target = action.getCommandTarget(UUID.fromString(tableUuid));
		EditColumnHeaderData colHeaderData = new EditColumnHeaderData();
		
		if (target != null){
			JRIdentifiable identifiable = target.getIdentifiable();
			JRDesignComponentElement componentElement = identifiable instanceof JRDesignComponentElement ? (JRDesignComponentElement)identifiable : null;
			StandardTable table = componentElement == null ? null : (StandardTable)componentElement.getComponent();
			
			List<BaseColumn> tableColumns = TableUtil.getAllColumns(table);
			
			if (columnIndex != null) {
				StandardColumn column = (StandardColumn) tableColumns.get(columnIndex);
				
				JRDesignTextElement textElement = TableUtil.getColumnHeaderTextElement(column);
				
				if (textElement != null) {
					colHeaderData.setHeadingName(JRStringUtil.htmlEncode(sortColumnLabel));
					colHeaderData.setColumnIndex(columnIndex);
					colHeaderData.setTableUuid(tableUuid);
					HeaderToolbarElementUtils.copyTextElementStyle(colHeaderData, textElement);
				}
			}
		}
		contextMap.put("colHeaderData", JacksonUtil.getInstance(jasperReportsContext).getJsonString(colHeaderData));
	}
	
	private void setColumnValueData(Integer columnIndex, String tableUuid, Map<String, Object> contextMap, JasperReportsContext jasperReportsContext, ReportContext reportContext) {
		FilterAction action = new FilterAction();
		action.init(jasperReportsContext, reportContext);
		CommandTarget target = action.getCommandTarget(UUID.fromString(tableUuid));
		EditColumnValueData colValueData = new EditColumnValueData();
		
		if (target != null){
			JRIdentifiable identifiable = target.getIdentifiable();
			JRDesignComponentElement componentElement = identifiable instanceof JRDesignComponentElement ? (JRDesignComponentElement)identifiable : null;
			StandardTable table = componentElement == null ? null : (StandardTable)componentElement.getComponent();
			
			List<BaseColumn> tableColumns = TableUtil.getAllColumns(table);
			
			if (columnIndex != null) {
				StandardColumn column = (StandardColumn) tableColumns.get(columnIndex);
				
				JRDesignTextField textElement = (JRDesignTextField)TableUtil.getColumnDetailTextElement(column);
				
				if (textElement != null) {
					colValueData.setColumnIndex(columnIndex);
					colValueData.setTableUuid(tableUuid);
					HeaderToolbarElementUtils.copyTextFieldStyle(colValueData, textElement);
				}
			}
		}
		contextMap.put("colValueData", JacksonUtil.getInstance(jasperReportsContext).getJsonString(colValueData));
	}

	public static class ColumnInfo {
		private String index;
		private String label;
		private String uuid;
		private boolean visible;
		private boolean interactive;
		
		private ColumnInfo(String index, String label, String uuid, boolean visible, boolean interactive) {
			this.index = index;
			this.label = label;
			this.uuid = uuid;
			this.visible = visible;
			this.interactive = interactive;
		}
		
		public String getIndex() {
			return index;
		}
		
		public String getLabel() {
			return label;
		}

		public String getUuid() {
			return uuid;
		}
		
		public boolean getVisible() {
			return visible;
		}

		public boolean getInteractive() {
			return interactive;
		}
	}

	private Map<String, ColumnInfo> getAllColumnNames(JRGenericPrintElement element, 
			JasperReportsContext jasperReportsContext, Map<String, Object> contextMap) {
		int prefixLength = HeaderToolbarElement.PARAM_COLUMN_LABEL_PREFIX.length();
		Map<String, ColumnInfo> columnNames = new HashMap<String, ColumnInfo>();
		for (String paramName : element.getParameterNames()) {
			if (paramName.startsWith(HeaderToolbarElement.PARAM_COLUMN_LABEL_PREFIX)) {
				String columnName = (String) element.getParameterValue(paramName);
				String[] tokens = paramName.substring(prefixLength).split("\\|");
				if (columnName == null || columnName.trim().length() == 0) {
					columnName = "Column_" + tokens[0];
				}
				columnNames.put(tokens[0], new ColumnInfo(tokens[0], JRStringUtil.htmlEncode(columnName), tokens[1], false, Boolean.valueOf(tokens[2])));
			}
		}
		return columnNames;
	}

	private List<Map<String, Object>> getColumnGroupsData(JasperReportsContext jasperReportsContext, ReportContext reportContext, String tableUuid) {
		FilterAction action = new FilterAction();
		action.init(jasperReportsContext, reportContext);
		CommandTarget target = action.getCommandTarget(UUID.fromString(tableUuid));
		List<Map<String, Object>> groupsData = new ArrayList<Map<String, Object>>();
        EditColumnHeaderData groupHeaderData;
        EditColumnValueData groupFooterData;
		
		if (target != null){
			JRIdentifiable identifiable = target.getIdentifiable();
			JRDesignComponentElement componentElement = identifiable instanceof JRDesignComponentElement ? (JRDesignComponentElement)identifiable : null;
			StandardTable table = componentElement == null ? null : (StandardTable)componentElement.getComponent();

			JRDesignDatasetRun datasetRun = (JRDesignDatasetRun)table.getDatasetRun();
			String datasetName = datasetRun.getDatasetName();
			JasperDesignCache cache = JasperDesignCache.getInstance(jasperReportsContext, reportContext);
			JasperDesign jasperDesign = cache.getJasperDesign(target.getUri());
			JRDesignDataset dataset = (JRDesignDataset)jasperDesign.getDatasetMap().get(datasetName);

			List<ColumnGroup> lst = TableUtil.getAllColumnGroups(table.getColumns());

			int i = 0, j;
			for (ColumnGroup cg: lst) {
				if (cg.getGroupHeaders() != null) {
                    j = 0;
					for (GroupCell gc: cg.getGroupHeaders()) {
						JRDesignTextElement textElement = TableUtil.getCellTextElement(gc.getCell(), false);
						
						if (textElement != null) {
							Map<String, Object> groupData = new HashMap<String, Object>();
                            groupHeaderData = new EditColumnHeaderData();
                            groupHeaderData.setTableUuid(tableUuid);
                            groupHeaderData.setI(i);
                            groupHeaderData.setJ(j);

							if (gc.getGroupName() != null && gc.getGroupName().length() > 0) {
                                groupHeaderData.setGroupName(gc.getGroupName());
							}

                            HeaderToolbarElementUtils.copyTextElementStyle(groupHeaderData, textElement);

							groupData.put("grouptype", "groupheading");
                            groupData.put("id", "groupheading_" + i + "_" + j);
                            groupData.put("groupData", groupHeaderData);
							groupsData.add(groupData);
						}
					}
				}
				if (cg.getGroupFooters() != null) {
                    j = 0;
					for (GroupCell gc: cg.getGroupFooters()) {
						JRTextField textElement = TableUtil.getCellDetailTextElement(gc.getCell(), false);
						FilterTypesEnum filterType = null;
						String fieldOrVariableName = null;
						
						if (textElement != null && TableUtil.hasSingleChunkExpression(textElement)) {
							JRExpressionChunk expression = textElement.getExpression().getChunks()[0];
							fieldOrVariableName = expression.getText();
                            groupFooterData = new EditColumnValueData();
                            groupFooterData.setTableUuid(tableUuid);
                            groupFooterData.setI(i);
                            groupFooterData.setJ(j);

                            switch (expression.getType()) {
                                case JRExpressionChunk.TYPE_FIELD:
                                    JRField field = getField(fieldOrVariableName, dataset);
                                    filterType = HeaderToolbarElementUtils.getFilterType(field.getValueClass());
                                    break;

                                case JRExpressionChunk.TYPE_VARIABLE:
                                    JRVariable variable = getVariable(fieldOrVariableName, dataset);
                                    filterType = HeaderToolbarElementUtils.getFilterType(variable.getValueClass());
                                    break;

                                default:
                                    // never
                                    throw new JRRuntimeException("Unrecognized expression type " + expression.getType());
                            }
                            groupFooterData.setDataType(filterType.getName());

                            Map<String, Object> groupData = new HashMap<String, Object>();

							if (gc.getGroupName() != null && gc.getGroupName().length() > 0) {
                                groupFooterData.setGroupName(gc.getGroupName());
							}

                            HeaderToolbarElementUtils.copyTextFieldStyle(groupFooterData, (JRDesignTextField)textElement);

							groupData.put("grouptype", "groupsubtotal");
                            groupData.put("id", "groupsubtotal_" + i + "_" + j);
                            groupData.put("groupData", groupFooterData);
							groupsData.add(groupData);
						}
					}
				}
				i++;
			}
		}
		
		return groupsData;
	}
	
	protected JRField getField(String name, JRDesignDataset dataSet) {
		JRField found = null;
		for (JRField field : dataSet.getFields())
		{
			if (name.equals(field.getName()))
			{
				found = field;
				break;
			}
		}
		return found;
	}
	
	protected JRVariable getVariable(String name, JRDesignDataset dataSet) {
		JRVariable found = null;
		for (JRVariable var : dataSet.getVariables())
		{
			if (name.equals(var.getName()))
			{
				found = var;
				break;
			}
		}
		return found;
	}
	
	private Set<String> getFontExtensionsFontNames(JasperReportsContext jasperReportsContext) {
		Set<String> classes = new TreeSet<String>(); 

        Collection<String> extensionFonts = FontUtil.getInstance(jasperReportsContext).getFontFamilyNames();
        for (Iterator<String> it = extensionFonts.iterator(); it.hasNext();) {
            String fname = it.next();
            classes.add(fname);
        }

        return classes;
    } 

	private Set<String> getSystemFontNames(JasperReportsContext jasperReportsContext) {
		Set<String> fontExtensionsFontNames = getFontExtensionsFontNames(jasperReportsContext);
		Set<String> classes = new TreeSet<String>();

		String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			if (fontExtensionsFontNames.add(name)) {
				classes.add(name);
			}
		}
		
		return classes;
	}
	
	private String getBundleMessage(String key, JasperReportsContext jasperReportsContext, String bundleName, Locale locale) {
		MessageProvider messageProvider = MessageUtil.getInstance(jasperReportsContext).getMessageProvider(bundleName);
		return messageProvider.getMessage(key, null, locale); 
	}

}

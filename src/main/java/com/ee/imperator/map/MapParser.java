package com.ee.imperator.map;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.ee.reflection.Builder;
import org.ee.text.PrimitiveUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ee.imperator.exception.MapParseException;
import com.ee.imperator.mission.GenericMission;
import com.ee.imperator.mission.Mission;
import com.ee.imperator.mission.VictoryCondition;

public class MapParser {
	private final File file;
	private int index = -1;
	private int id;
	private String name;
	private int players;
	private java.util.Map<String, String> descriptions;
	private java.util.Map<String, Territory> territories;
	private java.util.Map<String, Region> regions;
	private java.util.Map<Integer, Mission> missions;
	private List<Integer> missionDistribution;

	public MapParser(File file) {
		this.file = file;
	}

	public static java.util.Map<Integer, Map> parseMaps(File... files) throws MapParseException {
		java.util.Map<Integer, Map> maps = new HashMap<>();
		for(File file : files) {
			Map map = new MapParser(file).parse();
			maps.put(map.getId(), map);
		}
		return maps;
	}

	public Map parse() throws MapParseException {
		try {
			Document doc = readDocument();
			id = getIntAttribute(doc.getDocumentElement(), "id");
			NodeList children = doc.getDocumentElement().getChildNodes();
			skipTo(children, "name");
			name = getText(children.item(index));
			skipTo(children, "players");
			players = getInt(children.item(index));
			skipTo(children, "description");
			parseDescriptions(children);
			skipTo(children, "territories");
			Element territories = (Element) children.item(index);
			parseRegions(children);
			parseTerritories(territories);
			skipTo(children, "missions");
			parseMissions((Element) children.item(index));
		} catch (ClassCastException e) {
			throw new MapParseException("Expected node of type Element", e);
		}
		return new Map(id, name, players, descriptions, territories, regions, missions, missionDistribution);
	}

	private void skipTo(NodeList children, String name) throws MapParseException {
		index++;
		while(index < children.getLength()) {
			Node child = children.item(index);
			if(child.getNodeType() == Node.ELEMENT_NODE) {
				break;
			}
			index++;
		}
		Node item = children.item(index);
		if(!name.equals(item.getNodeName())) {
			throw new MapParseException("Expected <" + name + ">, found " + item.getNodeName());
		}
	}

	private String getText(Node item) throws MapParseException {
		if(item == null) {
			throw new MapParseException("Expected node");
		}
		String content = item.getTextContent();
		if(content == null || content.isEmpty()) {
			throw new MapParseException("Empty <" + item.getNodeName() + ">");
		}
		return content;
	}

	private int getInt(Node item) throws MapParseException {
		String value = getText(item);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new MapParseException("Value of <" + item.getNodeName() + "> should be of type int", e);
		}
	}

	private Document readDocument() throws MapParseException {
		try(InputStream input = new BufferedInputStream(new FileInputStream(file))) {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
		} catch (Exception e) {
			throw new MapParseException("Error reading document", e);
		}
	}

	private void parseDescriptions(NodeList children) throws MapParseException {
		descriptions = new HashMap<>();
		do {
			parseDescription((Element) children.item(index));
			index++;
		} while(nextIs(children, "description"));
		index--;
	}

	private boolean nextIs(NodeList children, String name) {
		Node node = children.item(index);
		while(node != null && node.getNodeType() != Node.ELEMENT_NODE) {
			node = children.item(++index);
		}
		return node != null && name.equals(node.getNodeName());
	}

	private void parseDescription(Element item) throws MapParseException {
		String lang = getAttribute(item, "xml:lang");
		descriptions.put(lang.toLowerCase(), item.getTextContent().trim());
	}

	private String getAttribute(Element item, String name) throws MapParseException {
		String attr = item.getAttribute(name);
		if(attr == null || attr.isEmpty()) {
			throw new MapParseException(item.getTagName() + " is missing attribute " + name);
		}
		return attr;
	}

	private int getIntAttribute(Element item, String name) throws MapParseException {
		try {
			return Integer.parseInt(getAttribute(item, name));
		} catch (NumberFormatException e) {
			throw new MapParseException("Attribute " + name + " on " + item.getTagName() + " must be of type int", e);
		}
	}

	private void parseRegions(NodeList children) throws MapParseException {
		regions = new HashMap<>();
		index++;
		if(nextIs(children, "regions")) {
			NodeList regions = ((Element) children.item(index)).getElementsByTagName("region");
			for(int i = 0; i < regions.getLength(); i++) {
				parseRegion((Element) regions.item(i));
			}
		} else {
			index--;
		}
	}

	private void parseRegion(Element item) throws MapParseException {
		Region region = new Region(item.getAttribute("id"), getText(item.getElementsByTagName("name").item(0)), getInt(item.getElementsByTagName("units").item(0)));
		regions.put(region.getId(), region);
	}

	private void parseTerritories(Element item) throws MapParseException {
		this.territories = new HashMap<>();
		NodeList territories = item.getElementsByTagName("territory");
		for(int i = 0; i < territories.getLength(); i++) {
			parseTerritory((Element) territories.item(i));
		}
		for(int i = 0; i < territories.getLength(); i++) {
			parseBorders((Element) territories.item(i));
		}
	}

	private void parseTerritory(Element item) throws MapParseException {
		Territory territory = new Territory(getAttribute(item, "id"), getText(item.getElementsByTagName("name").item(0)));
		territories.put(territory.getId(), territory);
		Element regions = (Element) item.getElementsByTagName("regions").item(0);
		if(regions != null) {
			NodeList regionNodes = regions.getElementsByTagName("region");
			for(int i = 0; i < regionNodes.getLength(); i++) {
				String id = getText(regionNodes.item(i));
				Region region = this.regions.get(id);
				if(region == null) {
					throw new MapParseException("Unknown region " + id);
				}
				region.getTerritories().add(territory);
				territory.getRegions().add(region);
			}
		}
	}

	private void parseBorders(Element item) throws MapParseException {
		Territory territory = territories.get(getAttribute(item, "id"));
		Element borders = (Element) item.getElementsByTagName("borders").item(0);
		if(borders == null) {
			throw new MapParseException("Territory is missing <borders>");
		}
		NodeList borderNodes = borders.getElementsByTagName("border");
		for(int i = 0; i < borderNodes.getLength(); i++) {
			String id = getText(borderNodes.item(i));
			Territory border = territories.get(id);
			if(border == null) {
				throw new MapParseException("Unknown border " + id + " for " + territory.getId());
			}
			territory.getBorders().add(border);
		}
	}

	private void parseMissions(Element item) throws MapParseException {
		this.missions = new HashMap<>();
		missionDistribution = new ArrayList<>();
		NodeList missions = item.getElementsByTagName("mission");
		for(int i = 0; i < missions.getLength(); i++) {
			parseMission((Element) missions.item(i));
		}
	}

	private void parseMission(Element item) throws MapParseException {
		int id = getIntAttribute(item, "missionId");
		int availability = getIntAttribute(item, "availability");
		while(availability > 0) {
			missionDistribution.add(id);
			availability--;
		}
		Mission mission;
		if(item.hasAttribute("class")) {
			mission = parseMissionWithClass(item, id);
		} else {
			mission = parseGenericMission(item, id);
		}
		if(item.hasAttribute("fallback")) {
			mission.setFallback(getIntAttribute(item, "fallback"));
		}
		missions.put(mission.getId(), mission);
	}

	private Mission parseMissionWithClass(Element item, int id) throws MapParseException {
		List<Object> values = new ArrayList<>();
		List<Class<?>> types = new ArrayList<>();
		values.add(id);
		types.add(int.class);
		Class<? extends Mission> missionClass = getMissionClass(getAttribute(item, "class"));
		try {
			return parseArguments(item, values, types).newInstance(missionClass);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new MapParseException("Failed to create mission from " + missionClass, e);
		}
	}

	private Builder parseArguments(Element item) throws MapParseException {
		return parseArguments(item, new ArrayList<>(), new ArrayList<>());
	}

	private Builder parseArguments(Element item, List<Object> values, List<Class<?>> types) throws MapParseException {
		NodeList argumentNodes = item.getElementsByTagName("argument");
		for(int i = 0; i < argumentNodes.getLength(); i++) {
			Element node = (Element) argumentNodes.item(i);
			Class<?> type = getArgumentType(node);
			String valueString = getText(node);
			if(type == String.class) {
				values.add(valueString);
			} else {
				try {
					values.add(PrimitiveUtils.parse(type, valueString));
				} catch (IllegalArgumentException e) {
					throw new MapParseException("Argument was not of expected type " + type, e);
				}
			}
			types.add(type);
		}
		return new Builder(types, values);
	}

	@SuppressWarnings("unchecked")
	private <E> Class<? extends E> getMissionClass(String name) throws MapParseException {
		try {
			if(name.contains(".")) {
				return (Class<? extends E>) Class.forName(name);
			}
			return (Class<? extends E>) Class.forName(Mission.class.getPackage().getName() + "." + name);
		} catch (ClassNotFoundException e) {
			throw new MapParseException("Unknown mission class", e);
		}
	}

	private Class<?> getArgumentType(Element node) throws MapParseException {
		if(node.hasAttribute("type")) {
			String type = getAttribute(node, "type");
			try {
				return PrimitiveUtils.getClass(type);
			} catch (IllegalArgumentException e) {
				throw new MapParseException("Non primitive argument type " + type, e);
			}
		}
		return String.class;
	}

	private Mission parseGenericMission(Element item, int id) throws MapParseException {
		String name = getText(item.getElementsByTagName("name").item(0));
		String description = getText(item.getElementsByTagName("description").item(0));
		List<VictoryCondition> conditions = new ArrayList<>();
		NodeList conditionNodes = item.getElementsByTagName("condition");
		for(int i = 0; i < conditionNodes.getLength(); i++) {
			conditions.add(parseMissionCondition((Element) conditionNodes.item(i)));
		}
		return new GenericMission(id, name, description, conditions);
	}

	private VictoryCondition parseMissionCondition(Element item) throws MapParseException {
		Class<? extends VictoryCondition> conditionClass = getMissionClass(getAttribute(item, "class"));
		try {
			return parseArguments(item).newInstance(conditionClass);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new MapParseException("Failed to create victory condition from " + conditionClass, e);
		}
	}
}

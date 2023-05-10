/**
 * 
 */
package com.jeepplus.bean;

/**
 * @author admin
 *
 */
public class Program {

	private String areaType;
	private String message;
	private String showId;
	private String text;
	private String fontName;
	private Integer x;
	private Integer y;
	private Integer width;
	private Integer height;
	private Integer color;
	private Integer fontSize;
	private Integer fontBold;
	private Integer stayTime;
	private Integer speed;
	private Integer displayStyle;
	private Integer textBinary;
	private Integer brightness;
	private String showTime = "60";
	private String ip;
	private Integer isMultLine = 1;
	private Integer programTime = 1500;
	private Integer dateFormat;
	private Integer timeFormat;
	private Integer weekFormat;

	private int runMode = 0;
	private int id = 1;
	private int immediatePlay = 1;

 

	/**
	 * 
	 */
	public Program() {
		super();
	}

	/**
     * @param areaType
     * @param message
     * @param showId
     * @param text
     * @param fontName
     * @param x
     * @param y
     * @param width
     * @param height
     * @param color
     * @param fontSize
     * @param fontBold
     * @param stayTime
     * @param speed
     * @param displayStyle
     * @param textBinary
     * @param brightness
     * @param showTime
     * @param ip
     * @param isMultLine
     * @param programTime
     * @param dateFormat
     * @param timeFormat
     * @param weekFormat
     * @param runMode
     * @param id
     * @param immediatePlay
     */
    public Program(String areaType, String message, String showId, String text, String fontName, Integer x, Integer y,
            Integer width, Integer height, Integer color, Integer fontSize, Integer fontBold, Integer stayTime,
            Integer speed, Integer displayStyle, Integer textBinary, Integer brightness, String showTime, String ip,
            Integer isMultLine, Integer programTime, Integer dateFormat, Integer timeFormat, Integer weekFormat,
            int runMode, int id, int immediatePlay) {
        super();
        this.areaType = areaType;
        this.message = message;
        this.showId = showId;
        this.text = text;
        this.fontName = fontName;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.fontSize = fontSize;
        this.fontBold = fontBold;
        this.stayTime = stayTime;
        this.speed = speed;
        this.displayStyle = displayStyle;
        this.textBinary = textBinary;
        this.brightness = brightness;
        this.showTime = showTime;
        this.ip = ip;
        this.isMultLine = isMultLine;
        this.programTime = programTime;
        this.dateFormat = dateFormat;
        this.timeFormat = timeFormat;
        this.weekFormat = weekFormat;
        this.runMode = runMode;
        this.id = id;
        this.immediatePlay = immediatePlay;
    }

    /**
	 * @return the programTime
	 */
	public Integer getProgramTime() {
		return programTime;
	}

	/**
	 * @param programTime the programTime to set
	 */
	public void setProgramTime(Integer programTime) {
		this.programTime = programTime;
	}

	/**
	 * @return the dateFormat
	 */
	public Integer getDateFormat() {
		return dateFormat;
	}

	/**
	 * @param dateFormat the dateFormat to set
	 */
	public void setDateFormat(Integer dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * @return the timeFormat
	 */
	public Integer getTimeFormat() {
		return timeFormat;
	}

	/**
	 * @param timeFormat the timeFormat to set
	 */
	public void setTimeFormat(Integer timeFormat) {
		this.timeFormat = timeFormat;
	}

	/**
	 * @return the weekFormat
	 */
	public Integer getWeekFormat() {
		return weekFormat;
	}

	/**
	 * @param weekFormat the weekFormat to set
	 */
	public void setWeekFormat(Integer weekFormat) {
		this.weekFormat = weekFormat;
	}

 

	/**
	 * @return the showId
	 */
	public String getShowId() {
		return showId;
	}

	/**
	 * @param showId the showId to set
	 */
	public void setShowId(String showId) {
		this.showId = showId;
	}

	/**
     * @return the isMultLine
     */
    public Integer getIsMultLine() {
        return isMultLine;
    }

    /**
     * @param isMultLine the isMultLine to set
     */
    public void setIsMultLine(Integer isMultLine) {
        this.isMultLine = isMultLine;
    }

    /**
	 * @return the brightness
	 */
	public Integer getBrightness() {
		return brightness;
	}

	/**
	 * @param brightness the brightness to set
	 */
	public void setBrightness(Integer brightness) {
		this.brightness = brightness;
	}

	/**
	 * @return the runMode
	 */
	public int getRunMode() {
		return runMode;
	}

	/**
	 * @param runMode the runMode to set
	 */
	public void setRunMode(int runMode) {
		this.runMode = runMode;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the immediatePlay
	 */
	public int getImmediatePlay() {
		return immediatePlay;
	}

	/**
	 * @param immediatePlay the immediatePlay to set
	 */
	public void setImmediatePlay(int immediatePlay) {
		this.immediatePlay = immediatePlay;
	}

	/**
	 * @return the showTime
	 */
	public String getShowTime() {
		return showTime;
	}

	/**
	 * @param showTime the showTime to set
	 */
	public void setShowTime(String showTime) {
		this.showTime = showTime;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the textBinary
	 */
	public Integer getTextBinary() {
		return textBinary;
	}

	/**
	 * @param textBinary the textBinary to set
	 */
	public void setTextBinary(Integer textBinary) {
		this.textBinary = textBinary;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the areaType
	 */
	public String getAreaType() {
		return areaType;
	}

	/**
	 * @param areaType the areaType to set
	 */
	public void setAreaType(String areaType) {
		this.areaType = areaType;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the fontName
	 */
	public String getFontName() {
		return fontName;
	}

	/**
	 * @param fontName the fontName to set
	 */
	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	/**
	 * @return the x
	 */
	public Integer getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(Integer x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public Integer getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(Integer y) {
		this.y = y;
	}

	/**
	 * @return the width
	 */
	public Integer getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(Integer width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public Integer getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(Integer height) {
		this.height = height;
	}

	/**
	 * @return the color
	 */
	public Integer getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Integer color) {
		this.color = color;
	}

	/**
	 * @return the fontSize
	 */
	public Integer getFontSize() {
		return fontSize;
	}

	/**
	 * @param fontSize the fontSize to set
	 */
	public void setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
	}

	/**
	 * @return the fontBold
	 */
	public Integer getFontBold() {
		return fontBold;
	}

	/**
	 * @param fontBold the fontBold to set
	 */
	public void setFontBold(Integer fontBold) {
		this.fontBold = fontBold;
	}

	/**
	 * @return the stayTime
	 */
	public Integer getStayTime() {
		return stayTime;
	}

	/**
	 * @param stayTime the stayTime to set
	 */
	public void setStayTime(Integer stayTime) {
		this.stayTime = stayTime;
	}

	/**
	 * @return the speed
	 */
	public Integer getSpeed() {
		return speed;
	}

	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(Integer speed) {
		this.speed = speed;
	}

	/**
	 * @return the displayStyle
	 */
	public Integer getDisplayStyle() {
		return displayStyle;
	}

	/**
	 * @param displayStyle the displayStyle to set
	 */
	public void setDisplayStyle(Integer displayStyle) {
		this.displayStyle = displayStyle;
	}

}

package br.com.binarti.sjog.model;

import java.util.Calendar;
import java.util.Date;

public class PojoWithAllPrimitivesAndWrappers {

	private boolean booleanPrimitive;
	private char charPrimitive;
	private byte bytePrimitive;
	private short shortPrimitive;
	private int intPrimitive;
	private long longPrimitive;
	private float floatPrimitive;
	private double doublePrimitive;

	private TestPrimitiveEnum enumAsPrimitive;

	private Boolean booleanWrapper;
	private Character characterWrapper;
	private String stringWrapper;
	private Byte byteWrapper;
	private Short shortWrapper;
	private Integer integerWrapper;
	private Long longWrapper;
	private Float floatWrapper;
	private Double doubleWrapper;

	private Date date;
	private Calendar calendar;

	public enum TestPrimitiveEnum {
		VAL1, VAL2;
	}

	public boolean isBooleanPrimitive() {
		return booleanPrimitive;
	}

	public void setBooleanPrimitive(boolean booleanPrimitive) {
		this.booleanPrimitive = booleanPrimitive;
	}

	public char getCharPrimitive() {
		return charPrimitive;
	}

	public void setCharPrimitive(char charPrimitive) {
		this.charPrimitive = charPrimitive;
	}

	public byte getBytePrimitive() {
		return bytePrimitive;
	}

	public void setBytePrimitive(byte bytePrimitive) {
		this.bytePrimitive = bytePrimitive;
	}

	public short getShortPrimitive() {
		return shortPrimitive;
	}

	public void setShortPrimitive(short shortPrimitive) {
		this.shortPrimitive = shortPrimitive;
	}

	public int getIntPrimitive() {
		return intPrimitive;
	}

	public void setIntPrimitive(int intPrimitive) {
		this.intPrimitive = intPrimitive;
	}

	public long getLongPrimitive() {
		return longPrimitive;
	}

	public void setLongPrimitive(long longPrimitive) {
		this.longPrimitive = longPrimitive;
	}

	public float getFloatPrimitive() {
		return floatPrimitive;
	}

	public void setFloatPrimitive(float floatPrimitive) {
		this.floatPrimitive = floatPrimitive;
	}

	public double getDoublePrimitive() {
		return doublePrimitive;
	}

	public void setDoublePrimitive(double doublePrimitive) {
		this.doublePrimitive = doublePrimitive;
	}

	public TestPrimitiveEnum getEnumAsPrimitive() {
		return enumAsPrimitive;
	}

	public void setEnumAsPrimitive(TestPrimitiveEnum enumAsPrimitive) {
		this.enumAsPrimitive = enumAsPrimitive;
	}

	public Boolean getBooleanWrapper() {
		return booleanWrapper;
	}

	public void setBooleanWrapper(Boolean booleanWrapper) {
		this.booleanWrapper = booleanWrapper;
	}

	public Character getCharacterWrapper() {
		return characterWrapper;
	}

	public void setCharacterWrapper(Character characterWrapper) {
		this.characterWrapper = characterWrapper;
	}

	public String getStringWrapper() {
		return stringWrapper;
	}

	public void setStringWrapper(String stringWrapper) {
		this.stringWrapper = stringWrapper;
	}

	public Byte getByteWrapper() {
		return byteWrapper;
	}

	public void setByteWrapper(Byte byteWrapper) {
		this.byteWrapper = byteWrapper;
	}

	public Short getShortWrapper() {
		return shortWrapper;
	}

	public void setShortWrapper(Short shortWrapper) {
		this.shortWrapper = shortWrapper;
	}

	public Integer getIntegerWrapper() {
		return integerWrapper;
	}

	public void setIntegerWrapper(Integer integerWrapper) {
		this.integerWrapper = integerWrapper;
	}

	public Long getLongWrapper() {
		return longWrapper;
	}

	public void setLongWrapper(Long longWrapper) {
		this.longWrapper = longWrapper;
	}

	public Float getFloatWrapper() {
		return floatWrapper;
	}

	public void setFloatWrapper(Float floatWrapper) {
		this.floatWrapper = floatWrapper;
	}

	public Double getDoubleWrapper() {
		return doubleWrapper;
	}

	public void setDoubleWrapper(Double doubleWrapper) {
		this.doubleWrapper = doubleWrapper;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

}

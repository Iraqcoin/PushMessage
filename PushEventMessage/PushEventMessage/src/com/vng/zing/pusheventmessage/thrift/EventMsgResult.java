/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vng.zing.pusheventmessage.thrift;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventMsgResult implements org.apache.thrift.TBase<EventMsgResult, EventMsgResult._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("EventMsgResult");

  private static final org.apache.thrift.protocol.TField RESULT_CODE_FIELD_DESC = new org.apache.thrift.protocol.TField("resultCode", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField EVENT_MSG_FIELD_DESC = new org.apache.thrift.protocol.TField("eventMsg", org.apache.thrift.protocol.TType.STRUCT, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new EventMsgResultStandardSchemeFactory());
    schemes.put(TupleScheme.class, new EventMsgResultTupleSchemeFactory());
  }

  public int resultCode; // required
  public EventMsg eventMsg; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    RESULT_CODE((short)1, "resultCode"),
    EVENT_MSG((short)2, "eventMsg");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // RESULT_CODE
          return RESULT_CODE;
        case 2: // EVENT_MSG
          return EVENT_MSG;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __RESULTCODE_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.RESULT_CODE, new org.apache.thrift.meta_data.FieldMetaData("resultCode", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.EVENT_MSG, new org.apache.thrift.meta_data.FieldMetaData("eventMsg", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, EventMsg.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(EventMsgResult.class, metaDataMap);
  }

  public EventMsgResult() {
  }

  public EventMsgResult(
    int resultCode,
    EventMsg eventMsg)
  {
    this();
    this.resultCode = resultCode;
    setResultCodeIsSet(true);
    this.eventMsg = eventMsg;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public EventMsgResult(EventMsgResult other) {
    __isset_bitfield = other.__isset_bitfield;
    this.resultCode = other.resultCode;
    if (other.isSetEventMsg()) {
      this.eventMsg = new EventMsg(other.eventMsg);
    }
  }

  public EventMsgResult deepCopy() {
    return new EventMsgResult(this);
  }

  @Override
  public void clear() {
    setResultCodeIsSet(false);
    this.resultCode = 0;
    this.eventMsg = null;
  }

  public int getResultCode() {
    return this.resultCode;
  }

  public EventMsgResult setResultCode(int resultCode) {
    this.resultCode = resultCode;
    setResultCodeIsSet(true);
    return this;
  }

  public void unsetResultCode() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __RESULTCODE_ISSET_ID);
  }

  /** Returns true if field resultCode is set (has been assigned a value) and false otherwise */
  public boolean isSetResultCode() {
    return EncodingUtils.testBit(__isset_bitfield, __RESULTCODE_ISSET_ID);
  }

  public void setResultCodeIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __RESULTCODE_ISSET_ID, value);
  }

  public EventMsg getEventMsg() {
    return this.eventMsg;
  }

  public EventMsgResult setEventMsg(EventMsg eventMsg) {
    this.eventMsg = eventMsg;
    return this;
  }

  public void unsetEventMsg() {
    this.eventMsg = null;
  }

  /** Returns true if field eventMsg is set (has been assigned a value) and false otherwise */
  public boolean isSetEventMsg() {
    return this.eventMsg != null;
  }

  public void setEventMsgIsSet(boolean value) {
    if (!value) {
      this.eventMsg = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case RESULT_CODE:
      if (value == null) {
        unsetResultCode();
      } else {
        setResultCode((Integer)value);
      }
      break;

    case EVENT_MSG:
      if (value == null) {
        unsetEventMsg();
      } else {
        setEventMsg((EventMsg)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case RESULT_CODE:
      return Integer.valueOf(getResultCode());

    case EVENT_MSG:
      return getEventMsg();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case RESULT_CODE:
      return isSetResultCode();
    case EVENT_MSG:
      return isSetEventMsg();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof EventMsgResult)
      return this.equals((EventMsgResult)that);
    return false;
  }

  public boolean equals(EventMsgResult that) {
    if (that == null)
      return false;

    boolean this_present_resultCode = true;
    boolean that_present_resultCode = true;
    if (this_present_resultCode || that_present_resultCode) {
      if (!(this_present_resultCode && that_present_resultCode))
        return false;
      if (this.resultCode != that.resultCode)
        return false;
    }

    boolean this_present_eventMsg = true && this.isSetEventMsg();
    boolean that_present_eventMsg = true && that.isSetEventMsg();
    if (this_present_eventMsg || that_present_eventMsg) {
      if (!(this_present_eventMsg && that_present_eventMsg))
        return false;
      if (!this.eventMsg.equals(that.eventMsg))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(EventMsgResult other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    EventMsgResult typedOther = (EventMsgResult)other;

    lastComparison = Boolean.valueOf(isSetResultCode()).compareTo(typedOther.isSetResultCode());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetResultCode()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.resultCode, typedOther.resultCode);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetEventMsg()).compareTo(typedOther.isSetEventMsg());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetEventMsg()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.eventMsg, typedOther.eventMsg);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("EventMsgResult(");
    boolean first = true;

    sb.append("resultCode:");
    sb.append(this.resultCode);
    first = false;
    if (!first) sb.append(", ");
    sb.append("eventMsg:");
    if (this.eventMsg == null) {
      sb.append("null");
    } else {
      sb.append(this.eventMsg);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
    if (eventMsg != null) {
      eventMsg.validate();
    }
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class EventMsgResultStandardSchemeFactory implements SchemeFactory {
    public EventMsgResultStandardScheme getScheme() {
      return new EventMsgResultStandardScheme();
    }
  }

  private static class EventMsgResultStandardScheme extends StandardScheme<EventMsgResult> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, EventMsgResult struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // RESULT_CODE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.resultCode = iprot.readI32();
              struct.setResultCodeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // EVENT_MSG
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.eventMsg = new EventMsg();
              struct.eventMsg.read(iprot);
              struct.setEventMsgIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, EventMsgResult struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(RESULT_CODE_FIELD_DESC);
      oprot.writeI32(struct.resultCode);
      oprot.writeFieldEnd();
      if (struct.eventMsg != null) {
        oprot.writeFieldBegin(EVENT_MSG_FIELD_DESC);
        struct.eventMsg.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class EventMsgResultTupleSchemeFactory implements SchemeFactory {
    public EventMsgResultTupleScheme getScheme() {
      return new EventMsgResultTupleScheme();
    }
  }

  private static class EventMsgResultTupleScheme extends TupleScheme<EventMsgResult> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, EventMsgResult struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetResultCode()) {
        optionals.set(0);
      }
      if (struct.isSetEventMsg()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetResultCode()) {
        oprot.writeI32(struct.resultCode);
      }
      if (struct.isSetEventMsg()) {
        struct.eventMsg.write(oprot);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, EventMsgResult struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.resultCode = iprot.readI32();
        struct.setResultCodeIsSet(true);
      }
      if (incoming.get(1)) {
        struct.eventMsg = new EventMsg();
        struct.eventMsg.read(iprot);
        struct.setEventMsgIsSet(true);
      }
    }
  }

}


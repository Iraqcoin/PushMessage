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

public class GetAllScheduledTaskResult implements org.apache.thrift.TBase<GetAllScheduledTaskResult, GetAllScheduledTaskResult._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("GetAllScheduledTaskResult");

  private static final org.apache.thrift.protocol.TField CODE_FIELD_DESC = new org.apache.thrift.protocol.TField("code", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField TASK_FIELD_DESC = new org.apache.thrift.protocol.TField("task", org.apache.thrift.protocol.TType.LIST, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new GetAllScheduledTaskResultStandardSchemeFactory());
    schemes.put(TupleScheme.class, new GetAllScheduledTaskResultTupleSchemeFactory());
  }

  public int code; // optional
  public List<ScheduledTask> task; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    CODE((short)1, "code"),
    TASK((short)2, "task");

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
        case 1: // CODE
          return CODE;
        case 2: // TASK
          return TASK;
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
  private static final int __CODE_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  private _Fields optionals[] = {_Fields.CODE,_Fields.TASK};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.CODE, new org.apache.thrift.meta_data.FieldMetaData("code", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.TASK, new org.apache.thrift.meta_data.FieldMetaData("task", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ScheduledTask.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(GetAllScheduledTaskResult.class, metaDataMap);
  }

  public GetAllScheduledTaskResult() {
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public GetAllScheduledTaskResult(GetAllScheduledTaskResult other) {
    __isset_bitfield = other.__isset_bitfield;
    this.code = other.code;
    if (other.isSetTask()) {
      List<ScheduledTask> __this__task = new ArrayList<ScheduledTask>();
      for (ScheduledTask other_element : other.task) {
        __this__task.add(new ScheduledTask(other_element));
      }
      this.task = __this__task;
    }
  }

  public GetAllScheduledTaskResult deepCopy() {
    return new GetAllScheduledTaskResult(this);
  }

  @Override
  public void clear() {
    setCodeIsSet(false);
    this.code = 0;
    this.task = null;
  }

  public int getCode() {
    return this.code;
  }

  public GetAllScheduledTaskResult setCode(int code) {
    this.code = code;
    setCodeIsSet(true);
    return this;
  }

  public void unsetCode() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __CODE_ISSET_ID);
  }

  /** Returns true if field code is set (has been assigned a value) and false otherwise */
  public boolean isSetCode() {
    return EncodingUtils.testBit(__isset_bitfield, __CODE_ISSET_ID);
  }

  public void setCodeIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __CODE_ISSET_ID, value);
  }

  public int getTaskSize() {
    return (this.task == null) ? 0 : this.task.size();
  }

  public java.util.Iterator<ScheduledTask> getTaskIterator() {
    return (this.task == null) ? null : this.task.iterator();
  }

  public void addToTask(ScheduledTask elem) {
    if (this.task == null) {
      this.task = new ArrayList<ScheduledTask>();
    }
    this.task.add(elem);
  }

  public List<ScheduledTask> getTask() {
    return this.task;
  }

  public GetAllScheduledTaskResult setTask(List<ScheduledTask> task) {
    this.task = task;
    return this;
  }

  public void unsetTask() {
    this.task = null;
  }

  /** Returns true if field task is set (has been assigned a value) and false otherwise */
  public boolean isSetTask() {
    return this.task != null;
  }

  public void setTaskIsSet(boolean value) {
    if (!value) {
      this.task = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case CODE:
      if (value == null) {
        unsetCode();
      } else {
        setCode((Integer)value);
      }
      break;

    case TASK:
      if (value == null) {
        unsetTask();
      } else {
        setTask((List<ScheduledTask>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case CODE:
      return Integer.valueOf(getCode());

    case TASK:
      return getTask();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case CODE:
      return isSetCode();
    case TASK:
      return isSetTask();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof GetAllScheduledTaskResult)
      return this.equals((GetAllScheduledTaskResult)that);
    return false;
  }

  public boolean equals(GetAllScheduledTaskResult that) {
    if (that == null)
      return false;

    boolean this_present_code = true && this.isSetCode();
    boolean that_present_code = true && that.isSetCode();
    if (this_present_code || that_present_code) {
      if (!(this_present_code && that_present_code))
        return false;
      if (this.code != that.code)
        return false;
    }

    boolean this_present_task = true && this.isSetTask();
    boolean that_present_task = true && that.isSetTask();
    if (this_present_task || that_present_task) {
      if (!(this_present_task && that_present_task))
        return false;
      if (!this.task.equals(that.task))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(GetAllScheduledTaskResult other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    GetAllScheduledTaskResult typedOther = (GetAllScheduledTaskResult)other;

    lastComparison = Boolean.valueOf(isSetCode()).compareTo(typedOther.isSetCode());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCode()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.code, typedOther.code);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTask()).compareTo(typedOther.isSetTask());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTask()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.task, typedOther.task);
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
    StringBuilder sb = new StringBuilder("GetAllScheduledTaskResult(");
    boolean first = true;

    if (isSetCode()) {
      sb.append("code:");
      sb.append(this.code);
      first = false;
    }
    if (isSetTask()) {
      if (!first) sb.append(", ");
      sb.append("task:");
      if (this.task == null) {
        sb.append("null");
      } else {
        sb.append(this.task);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
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

  private static class GetAllScheduledTaskResultStandardSchemeFactory implements SchemeFactory {
    public GetAllScheduledTaskResultStandardScheme getScheme() {
      return new GetAllScheduledTaskResultStandardScheme();
    }
  }

  private static class GetAllScheduledTaskResultStandardScheme extends StandardScheme<GetAllScheduledTaskResult> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, GetAllScheduledTaskResult struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // CODE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.code = iprot.readI32();
              struct.setCodeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // TASK
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list144 = iprot.readListBegin();
                struct.task = new ArrayList<ScheduledTask>(_list144.size);
                for (int _i145 = 0; _i145 < _list144.size; ++_i145)
                {
                  ScheduledTask _elem146; // required
                  _elem146 = new ScheduledTask();
                  _elem146.read(iprot);
                  struct.task.add(_elem146);
                }
                iprot.readListEnd();
              }
              struct.setTaskIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, GetAllScheduledTaskResult struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.isSetCode()) {
        oprot.writeFieldBegin(CODE_FIELD_DESC);
        oprot.writeI32(struct.code);
        oprot.writeFieldEnd();
      }
      if (struct.task != null) {
        if (struct.isSetTask()) {
          oprot.writeFieldBegin(TASK_FIELD_DESC);
          {
            oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.task.size()));
            for (ScheduledTask _iter147 : struct.task)
            {
              _iter147.write(oprot);
            }
            oprot.writeListEnd();
          }
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class GetAllScheduledTaskResultTupleSchemeFactory implements SchemeFactory {
    public GetAllScheduledTaskResultTupleScheme getScheme() {
      return new GetAllScheduledTaskResultTupleScheme();
    }
  }

  private static class GetAllScheduledTaskResultTupleScheme extends TupleScheme<GetAllScheduledTaskResult> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, GetAllScheduledTaskResult struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetCode()) {
        optionals.set(0);
      }
      if (struct.isSetTask()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetCode()) {
        oprot.writeI32(struct.code);
      }
      if (struct.isSetTask()) {
        {
          oprot.writeI32(struct.task.size());
          for (ScheduledTask _iter148 : struct.task)
          {
            _iter148.write(oprot);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, GetAllScheduledTaskResult struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.code = iprot.readI32();
        struct.setCodeIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TList _list149 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.task = new ArrayList<ScheduledTask>(_list149.size);
          for (int _i150 = 0; _i150 < _list149.size; ++_i150)
          {
            ScheduledTask _elem151; // required
            _elem151 = new ScheduledTask();
            _elem151.read(iprot);
            struct.task.add(_elem151);
          }
        }
        struct.setTaskIsSet(true);
      }
    }
  }

}


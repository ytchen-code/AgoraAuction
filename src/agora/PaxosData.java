package agora;

/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2020-04-22")
public class PaxosData implements org.apache.thrift.TBase<PaxosData, PaxosData._Fields>, java.io.Serializable, Cloneable, Comparable<PaxosData> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("PaxosData");

  private static final org.apache.thrift.protocol.TField N_FIELD_DESC = new org.apache.thrift.protocol.TField("n", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField V_FIELD_DESC = new org.apache.thrift.protocol.TField("v", org.apache.thrift.protocol.TType.STRUCT, (short)2);
  private static final org.apache.thrift.protocol.TField NACK_FIELD_DESC = new org.apache.thrift.protocol.TField("nack", org.apache.thrift.protocol.TType.BOOL, (short)3);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new PaxosDataStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new PaxosDataTupleSchemeFactory();

  public int n; // required
  public @org.apache.thrift.annotation.Nullable Value v; // required
  public boolean nack; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    N((short)1, "n"),
    V((short)2, "v"),
    NACK((short)3, "nack");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // N
          return N;
        case 2: // V
          return V;
        case 3: // NACK
          return NACK;
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
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __N_ISSET_ID = 0;
  private static final int __NACK_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.N, new org.apache.thrift.meta_data.FieldMetaData("n", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.V, new org.apache.thrift.meta_data.FieldMetaData("v", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRUCT        , "Value")));
    tmpMap.put(_Fields.NACK, new org.apache.thrift.meta_data.FieldMetaData("nack", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(PaxosData.class, metaDataMap);
  }

  public PaxosData() {
  }

  public PaxosData(
    int n,
    Value v,
    boolean nack)
  {
    this();
    this.n = n;
    setNIsSet(true);
    this.v = v;
    this.nack = nack;
    setNackIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public PaxosData(PaxosData other) {
    __isset_bitfield = other.__isset_bitfield;
    this.n = other.n;
    if (other.isSetV()) {
      this.v = new Value(other.v);
    }
    this.nack = other.nack;
  }

  public PaxosData deepCopy() {
    return new PaxosData(this);
  }

  @Override
  public void clear() {
    setNIsSet(false);
    this.n = 0;
    this.v = null;
    setNackIsSet(false);
    this.nack = false;
  }

  public int getN() {
    return this.n;
  }

  public PaxosData setN(int n) {
    this.n = n;
    setNIsSet(true);
    return this;
  }

  public void unsetN() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __N_ISSET_ID);
  }

  /** Returns true if field n is set (has been assigned a value) and false otherwise */
  public boolean isSetN() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __N_ISSET_ID);
  }

  public void setNIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __N_ISSET_ID, value);
  }

  @org.apache.thrift.annotation.Nullable
  public Value getV() {
    return this.v;
  }

  public PaxosData setV(@org.apache.thrift.annotation.Nullable Value v) {
    this.v = v;
    return this;
  }

  public void unsetV() {
    this.v = null;
  }

  /** Returns true if field v is set (has been assigned a value) and false otherwise */
  public boolean isSetV() {
    return this.v != null;
  }

  public void setVIsSet(boolean value) {
    if (!value) {
      this.v = null;
    }
  }

  public boolean isNack() {
    return this.nack;
  }

  public PaxosData setNack(boolean nack) {
    this.nack = nack;
    setNackIsSet(true);
    return this;
  }

  public void unsetNack() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __NACK_ISSET_ID);
  }

  /** Returns true if field nack is set (has been assigned a value) and false otherwise */
  public boolean isSetNack() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __NACK_ISSET_ID);
  }

  public void setNackIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __NACK_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case N:
      if (value == null) {
        unsetN();
      } else {
        setN((java.lang.Integer)value);
      }
      break;

    case V:
      if (value == null) {
        unsetV();
      } else {
        setV((Value)value);
      }
      break;

    case NACK:
      if (value == null) {
        unsetNack();
      } else {
        setNack((java.lang.Boolean)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case N:
      return getN();

    case V:
      return getV();

    case NACK:
      return isNack();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case N:
      return isSetN();
    case V:
      return isSetV();
    case NACK:
      return isSetNack();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof PaxosData)
      return this.equals((PaxosData)that);
    return false;
  }

  public boolean equals(PaxosData that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_n = true;
    boolean that_present_n = true;
    if (this_present_n || that_present_n) {
      if (!(this_present_n && that_present_n))
        return false;
      if (this.n != that.n)
        return false;
    }

    boolean this_present_v = true && this.isSetV();
    boolean that_present_v = true && that.isSetV();
    if (this_present_v || that_present_v) {
      if (!(this_present_v && that_present_v))
        return false;
      if (!this.v.equals(that.v))
        return false;
    }

    boolean this_present_nack = true;
    boolean that_present_nack = true;
    if (this_present_nack || that_present_nack) {
      if (!(this_present_nack && that_present_nack))
        return false;
      if (this.nack != that.nack)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + n;

    hashCode = hashCode * 8191 + ((isSetV()) ? 131071 : 524287);
    if (isSetV())
      hashCode = hashCode * 8191 + v.hashCode();

    hashCode = hashCode * 8191 + ((nack) ? 131071 : 524287);

    return hashCode;
  }

  @Override
  public int compareTo(PaxosData other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetN()).compareTo(other.isSetN());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetN()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.n, other.n);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetV()).compareTo(other.isSetV());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetV()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.v, other.v);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetNack()).compareTo(other.isSetNack());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetNack()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.nack, other.nack);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("PaxosData(");
    boolean first = true;

    sb.append("n:");
    sb.append(this.n);
    first = false;
    if (!first) sb.append(", ");
    sb.append("v:");
    if (this.v == null) {
      sb.append("null");
    } else {
      sb.append(this.v);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("nack:");
    sb.append(this.nack);
    first = false;
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

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class PaxosDataStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public PaxosDataStandardScheme getScheme() {
      return new PaxosDataStandardScheme();
    }
  }

  private static class PaxosDataStandardScheme extends org.apache.thrift.scheme.StandardScheme<PaxosData> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, PaxosData struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // N
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.n = iprot.readI32();
              struct.setNIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // V
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.v = new Value();
              struct.v.read(iprot);
              struct.setVIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // NACK
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.nack = iprot.readBool();
              struct.setNackIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, PaxosData struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(N_FIELD_DESC);
      oprot.writeI32(struct.n);
      oprot.writeFieldEnd();
      if (struct.v != null) {
        oprot.writeFieldBegin(V_FIELD_DESC);
        struct.v.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(NACK_FIELD_DESC);
      oprot.writeBool(struct.nack);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class PaxosDataTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public PaxosDataTupleScheme getScheme() {
      return new PaxosDataTupleScheme();
    }
  }

  private static class PaxosDataTupleScheme extends org.apache.thrift.scheme.TupleScheme<PaxosData> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, PaxosData struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetN()) {
        optionals.set(0);
      }
      if (struct.isSetV()) {
        optionals.set(1);
      }
      if (struct.isSetNack()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetN()) {
        oprot.writeI32(struct.n);
      }
      if (struct.isSetV()) {
        struct.v.write(oprot);
      }
      if (struct.isSetNack()) {
        oprot.writeBool(struct.nack);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, PaxosData struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.n = iprot.readI32();
        struct.setNIsSet(true);
      }
      if (incoming.get(1)) {
        struct.v = new Value();
        struct.v.read(iprot);
        struct.setVIsSet(true);
      }
      if (incoming.get(2)) {
        struct.nack = iprot.readBool();
        struct.setNackIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}


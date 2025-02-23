// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Calculator.proto

package proto;

public final class CalculatorOuterClass {
  private CalculatorOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface NumberOrBuilder extends
      // @@protoc_insertion_point(interface_extends:Number)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>float value = 1;</code>
     */
    float getValue();
  }
  /**
   * Protobuf type {@code Number}
   */
  public  static final class Number extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:Number)
      NumberOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Number.newBuilder() to construct.
    private Number(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Number() {
      value_ = 0F;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Number(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 13: {

              value_ = input.readFloat();
              break;
            }
            default: {
              if (!parseUnknownFieldProto3(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return proto.CalculatorOuterClass.internal_static_Number_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return proto.CalculatorOuterClass.internal_static_Number_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              proto.CalculatorOuterClass.Number.class, proto.CalculatorOuterClass.Number.Builder.class);
    }

    public static final int VALUE_FIELD_NUMBER = 1;
    private float value_;
    /**
     * <code>float value = 1;</code>
     */
    public float getValue() {
      return value_;
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (value_ != 0F) {
        output.writeFloat(1, value_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (value_ != 0F) {
        size += com.google.protobuf.CodedOutputStream
          .computeFloatSize(1, value_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof proto.CalculatorOuterClass.Number)) {
        return super.equals(obj);
      }
      proto.CalculatorOuterClass.Number other = (proto.CalculatorOuterClass.Number) obj;

      boolean result = true;
      result = result && (
          java.lang.Float.floatToIntBits(getValue())
          == java.lang.Float.floatToIntBits(
              other.getValue()));
      result = result && unknownFields.equals(other.unknownFields);
      return result;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + VALUE_FIELD_NUMBER;
      hash = (53 * hash) + java.lang.Float.floatToIntBits(
          getValue());
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static proto.CalculatorOuterClass.Number parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.CalculatorOuterClass.Number parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.CalculatorOuterClass.Number parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.CalculatorOuterClass.Number parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.CalculatorOuterClass.Number parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.CalculatorOuterClass.Number parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.CalculatorOuterClass.Number parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static proto.CalculatorOuterClass.Number parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static proto.CalculatorOuterClass.Number parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static proto.CalculatorOuterClass.Number parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static proto.CalculatorOuterClass.Number parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static proto.CalculatorOuterClass.Number parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(proto.CalculatorOuterClass.Number prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code Number}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:Number)
        proto.CalculatorOuterClass.NumberOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return proto.CalculatorOuterClass.internal_static_Number_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return proto.CalculatorOuterClass.internal_static_Number_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                proto.CalculatorOuterClass.Number.class, proto.CalculatorOuterClass.Number.Builder.class);
      }

      // Construct using proto.CalculatorOuterClass.Number.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        value_ = 0F;

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return proto.CalculatorOuterClass.internal_static_Number_descriptor;
      }

      @java.lang.Override
      public proto.CalculatorOuterClass.Number getDefaultInstanceForType() {
        return proto.CalculatorOuterClass.Number.getDefaultInstance();
      }

      @java.lang.Override
      public proto.CalculatorOuterClass.Number build() {
        proto.CalculatorOuterClass.Number result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public proto.CalculatorOuterClass.Number buildPartial() {
        proto.CalculatorOuterClass.Number result = new proto.CalculatorOuterClass.Number(this);
        result.value_ = value_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return (Builder) super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return (Builder) super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof proto.CalculatorOuterClass.Number) {
          return mergeFrom((proto.CalculatorOuterClass.Number)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(proto.CalculatorOuterClass.Number other) {
        if (other == proto.CalculatorOuterClass.Number.getDefaultInstance()) return this;
        if (other.getValue() != 0F) {
          setValue(other.getValue());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        proto.CalculatorOuterClass.Number parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (proto.CalculatorOuterClass.Number) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private float value_ ;
      /**
       * <code>float value = 1;</code>
       */
      public float getValue() {
        return value_;
      }
      /**
       * <code>float value = 1;</code>
       */
      public Builder setValue(float value) {
        
        value_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>float value = 1;</code>
       */
      public Builder clearValue() {
        
        value_ = 0F;
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFieldsProto3(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:Number)
    }

    // @@protoc_insertion_point(class_scope:Number)
    private static final proto.CalculatorOuterClass.Number DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new proto.CalculatorOuterClass.Number();
    }

    public static proto.CalculatorOuterClass.Number getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Number>
        PARSER = new com.google.protobuf.AbstractParser<Number>() {
      @java.lang.Override
      public Number parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Number(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Number> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Number> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public proto.CalculatorOuterClass.Number getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface SumNumbersOrBuilder extends
      // @@protoc_insertion_point(interface_extends:SumNumbers)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>float a = 1;</code>
     */
    float getA();

    /**
     * <code>float b = 2;</code>
     */
    float getB();
  }
  /**
   * Protobuf type {@code SumNumbers}
   */
  public  static final class SumNumbers extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:SumNumbers)
      SumNumbersOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use SumNumbers.newBuilder() to construct.
    private SumNumbers(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private SumNumbers() {
      a_ = 0F;
      b_ = 0F;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private SumNumbers(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 13: {

              a_ = input.readFloat();
              break;
            }
            case 21: {

              b_ = input.readFloat();
              break;
            }
            default: {
              if (!parseUnknownFieldProto3(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return proto.CalculatorOuterClass.internal_static_SumNumbers_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return proto.CalculatorOuterClass.internal_static_SumNumbers_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              proto.CalculatorOuterClass.SumNumbers.class, proto.CalculatorOuterClass.SumNumbers.Builder.class);
    }

    public static final int A_FIELD_NUMBER = 1;
    private float a_;
    /**
     * <code>float a = 1;</code>
     */
    public float getA() {
      return a_;
    }

    public static final int B_FIELD_NUMBER = 2;
    private float b_;
    /**
     * <code>float b = 2;</code>
     */
    public float getB() {
      return b_;
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (a_ != 0F) {
        output.writeFloat(1, a_);
      }
      if (b_ != 0F) {
        output.writeFloat(2, b_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (a_ != 0F) {
        size += com.google.protobuf.CodedOutputStream
          .computeFloatSize(1, a_);
      }
      if (b_ != 0F) {
        size += com.google.protobuf.CodedOutputStream
          .computeFloatSize(2, b_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof proto.CalculatorOuterClass.SumNumbers)) {
        return super.equals(obj);
      }
      proto.CalculatorOuterClass.SumNumbers other = (proto.CalculatorOuterClass.SumNumbers) obj;

      boolean result = true;
      result = result && (
          java.lang.Float.floatToIntBits(getA())
          == java.lang.Float.floatToIntBits(
              other.getA()));
      result = result && (
          java.lang.Float.floatToIntBits(getB())
          == java.lang.Float.floatToIntBits(
              other.getB()));
      result = result && unknownFields.equals(other.unknownFields);
      return result;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + A_FIELD_NUMBER;
      hash = (53 * hash) + java.lang.Float.floatToIntBits(
          getA());
      hash = (37 * hash) + B_FIELD_NUMBER;
      hash = (53 * hash) + java.lang.Float.floatToIntBits(
          getB());
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static proto.CalculatorOuterClass.SumNumbers parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.CalculatorOuterClass.SumNumbers parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.CalculatorOuterClass.SumNumbers parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.CalculatorOuterClass.SumNumbers parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.CalculatorOuterClass.SumNumbers parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static proto.CalculatorOuterClass.SumNumbers parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static proto.CalculatorOuterClass.SumNumbers parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static proto.CalculatorOuterClass.SumNumbers parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static proto.CalculatorOuterClass.SumNumbers parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static proto.CalculatorOuterClass.SumNumbers parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static proto.CalculatorOuterClass.SumNumbers parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static proto.CalculatorOuterClass.SumNumbers parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(proto.CalculatorOuterClass.SumNumbers prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code SumNumbers}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:SumNumbers)
        proto.CalculatorOuterClass.SumNumbersOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return proto.CalculatorOuterClass.internal_static_SumNumbers_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return proto.CalculatorOuterClass.internal_static_SumNumbers_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                proto.CalculatorOuterClass.SumNumbers.class, proto.CalculatorOuterClass.SumNumbers.Builder.class);
      }

      // Construct using proto.CalculatorOuterClass.SumNumbers.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        a_ = 0F;

        b_ = 0F;

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return proto.CalculatorOuterClass.internal_static_SumNumbers_descriptor;
      }

      @java.lang.Override
      public proto.CalculatorOuterClass.SumNumbers getDefaultInstanceForType() {
        return proto.CalculatorOuterClass.SumNumbers.getDefaultInstance();
      }

      @java.lang.Override
      public proto.CalculatorOuterClass.SumNumbers build() {
        proto.CalculatorOuterClass.SumNumbers result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public proto.CalculatorOuterClass.SumNumbers buildPartial() {
        proto.CalculatorOuterClass.SumNumbers result = new proto.CalculatorOuterClass.SumNumbers(this);
        result.a_ = a_;
        result.b_ = b_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return (Builder) super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return (Builder) super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof proto.CalculatorOuterClass.SumNumbers) {
          return mergeFrom((proto.CalculatorOuterClass.SumNumbers)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(proto.CalculatorOuterClass.SumNumbers other) {
        if (other == proto.CalculatorOuterClass.SumNumbers.getDefaultInstance()) return this;
        if (other.getA() != 0F) {
          setA(other.getA());
        }
        if (other.getB() != 0F) {
          setB(other.getB());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        proto.CalculatorOuterClass.SumNumbers parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (proto.CalculatorOuterClass.SumNumbers) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private float a_ ;
      /**
       * <code>float a = 1;</code>
       */
      public float getA() {
        return a_;
      }
      /**
       * <code>float a = 1;</code>
       */
      public Builder setA(float value) {
        
        a_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>float a = 1;</code>
       */
      public Builder clearA() {
        
        a_ = 0F;
        onChanged();
        return this;
      }

      private float b_ ;
      /**
       * <code>float b = 2;</code>
       */
      public float getB() {
        return b_;
      }
      /**
       * <code>float b = 2;</code>
       */
      public Builder setB(float value) {
        
        b_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>float b = 2;</code>
       */
      public Builder clearB() {
        
        b_ = 0F;
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFieldsProto3(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:SumNumbers)
    }

    // @@protoc_insertion_point(class_scope:SumNumbers)
    private static final proto.CalculatorOuterClass.SumNumbers DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new proto.CalculatorOuterClass.SumNumbers();
    }

    public static proto.CalculatorOuterClass.SumNumbers getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<SumNumbers>
        PARSER = new com.google.protobuf.AbstractParser<SumNumbers>() {
      @java.lang.Override
      public SumNumbers parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new SumNumbers(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<SumNumbers> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<SumNumbers> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public proto.CalculatorOuterClass.SumNumbers getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_Number_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_Number_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_SumNumbers_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_SumNumbers_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\020Calculator.proto\"\027\n\006Number\022\r\n\005value\030\001 " +
      "\001(\002\"\"\n\nSumNumbers\022\t\n\001a\030\001 \001(\002\022\t\n\001b\030\002 \001(\0022" +
      "r\n\nCalculator\022 \n\nSquareRoot\022\007.Number\032\007.N" +
      "umber\"\000\022#\n\rAbsoluteValue\022\007.Number\032\007.Numb" +
      "er\"\000\022\035\n\003Sum\022\013.SumNumbers\032\007.Number\"\000B\007\n\005p" +
      "rotob\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_Number_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_Number_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_Number_descriptor,
        new java.lang.String[] { "Value", });
    internal_static_SumNumbers_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_SumNumbers_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_SumNumbers_descriptor,
        new java.lang.String[] { "A", "B", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}

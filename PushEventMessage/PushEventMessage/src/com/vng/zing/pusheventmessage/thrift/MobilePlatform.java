/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vng.zing.pusheventmessage.thrift;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum MobilePlatform implements org.apache.thrift.TEnum {
  ANDROID(0),
  IOS(1),
  WPHONE(2);

  private final int value;

  private MobilePlatform(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static MobilePlatform findByValue(int value) { 
    switch (value) {
      case 0:
        return ANDROID;
      case 1:
        return IOS;
      case 2:
        return WPHONE;
      default:
        return null;
    }
  }
}

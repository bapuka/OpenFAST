/*
The contents of this file are subject to the Mozilla Public License
Version 1.1 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenFAST.

The Initial Developer of the Original Code is The LaSalle Technology
Group, LLC.  Portions created by The LaSalle Technology Group, LLC
are Copyright (C) The LaSalle Technology Group, LLC. All Rights Reserved.

Contributor(s): Jacob Northey <jacob@lasalletech.com>
                Craig Otis <cotis@lasalletech.com>
*/


package org.openfast.template.type;

import org.openfast.ScalarValue;

import org.openfast.template.operator.Operator;
import org.openfast.template.operator.TwinOperator;

import org.openfast.util.Key;

import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;


public abstract class Type {
    static final int NULL_SCALED_NUMBER = -64;
    protected static final byte[] NULL_SF_DECIMAL_VALUE_ENCODING = new byte[] {
            (byte) 0xc0
        };
    protected static final byte[] NULL_TF_DECIMAL_VALUE_ENCODING = new byte[] {
            (byte) 0xc0, (byte) 0x80
        };
    protected static final byte STOP_BIT = (byte) 0x80;
    static final byte[] NULL_VALUE_ENCODING = new byte[] { STOP_BIT };

    // Type Enumerations
    public static final String U32 = "u32";
    public static final String I32 = "i32";
    public static final String DECIMAL = "decimal";
    public static final String STRING = "string";
    public static final String BYTE_VECTOR = "byte";

    // Type Collections
    public static final String[] ALL_TYPES = new String[] {
            U32, I32, DECIMAL, STRING, BYTE_VECTOR
        };
    public static final String[] INTEGER_TYPES = new String[] {
            Type.U32, Type.I32
        };

    // Type Definitions
    public static final Type UINT = new UnsignedInteger();
    public static final Type NULLABLE_UNSIGNED_INTEGER = new NullableUnsignedInteger();
    public static final Type INTEGER = new SignedInteger();
    public static final Type NULLABLE_INTEGER = new NullableSignedInteger();
    public static final Type STRING_TYPE = new StringType();
    public static final Type NULLABLE_STRING_TYPE = new NullableStringType();
    public static final Type BIT_VECTOR = new BitVectorType();
    public static final Type BYTE_VECTOR_TYPE = new ByteVectorType();
    public static final Type SF_SCALED_NUMBER = new SingleFieldDecimal();
    public static final Type NULLABLE_SF_SCALED_NUMBER = new NullableSingleFieldDecimal();
    public static final Type TF_SCALED_NUMBER = new TwinFieldDecimal();
    public static final Type STRING_DELTA = new StringDelta();
    public static final Type NULLABLE_STRING_DELTA = new NullableStringDelta();
    private static final Map TYPE_MAP = new HashMap();
	private static final Map NAME_MAP = new HashMap();

    static {
        TYPE_MAP.put(new Key(I32, Boolean.TRUE), NULLABLE_INTEGER);
        TYPE_MAP.put(new Key(I32, Boolean.FALSE), INTEGER);
        TYPE_MAP.put(new Key(U32, Boolean.TRUE),
            NULLABLE_UNSIGNED_INTEGER);
        TYPE_MAP.put(new Key(U32, Boolean.FALSE), UINT);
        TYPE_MAP.put(new Key(DECIMAL, Boolean.TRUE), NULLABLE_SF_SCALED_NUMBER);
        TYPE_MAP.put(new Key(DECIMAL, Boolean.FALSE), SF_SCALED_NUMBER);
        TYPE_MAP.put(new Key(STRING, Boolean.TRUE), NULLABLE_STRING_TYPE);
        TYPE_MAP.put(new Key(STRING, Boolean.FALSE), STRING_TYPE);
        TYPE_MAP.put(new Key(BYTE_VECTOR, Boolean.TRUE), BYTE_VECTOR_TYPE);
        TYPE_MAP.put(new Key(BYTE_VECTOR, Boolean.FALSE), BYTE_VECTOR_TYPE);
        
        NAME_MAP.put(U32, "unsigned integer");
        NAME_MAP.put(I32, "signed integer");
        NAME_MAP.put(DECIMAL, "decimal");
        NAME_MAP.put(STRING, "string");
        NAME_MAP.put(BYTE_VECTOR, "byte vector");
    }

    public abstract byte[] encodeValue(ScalarValue value);

    public byte[] encode(ScalarValue value) {
        byte[] encoding = encodeValue(value);
        encoding[encoding.length - 1] |= 0x80; // add stop bit;

        return encoding;
    }

    public abstract ScalarValue parse(String value);

    public abstract ScalarValue decode(InputStream in);

    public boolean isNullable() {
    	return false;
    }

    public static Type getType(String type, boolean optional, Operator operator) {
        Key key = new Key(type, Boolean.valueOf(optional));

        if (operator instanceof TwinOperator) {
            if (type != DECIMAL) {
                throw new IllegalArgumentException(
                    "Twin field operators can only be used for decimal values.");
            }

            return TF_SCALED_NUMBER;
        } else if (operator == Operator.DELTA_INTEGER) {
            return (optional) ? NULLABLE_INTEGER : INTEGER;
        } else if (operator == Operator.DELTA_STRING) {
            return (optional) ? NULLABLE_STRING_DELTA : STRING_DELTA;
        }

        if (!TYPE_MAP.containsKey(key)) {
            throw new IllegalArgumentException(
                "The type specified does not exist.");
        }

        return (Type) TYPE_MAP.get(key);
    }

    public abstract ScalarValue getDefaultValue();

	public static String getTypeName(Integer type) {
		return (String) NAME_MAP.get(type);
	}
}
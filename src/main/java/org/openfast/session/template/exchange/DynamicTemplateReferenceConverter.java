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
package org.openfast.session.template.exchange;

import org.openfast.GroupValue;
import org.openfast.session.SessionControlProtocol_1_1;
import org.openfast.template.DynamicTemplateReference;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.TemplateRegistry;

public class DynamicTemplateReferenceConverter implements FieldInstructionConverter {
    public Field convert(GroupValue groupValue, TemplateRegistry templateRegistry, ConversionContext context) {
        return DynamicTemplateReference.INSTANCE;
    }

    public GroupValue convert(Field field, ConversionContext context) {
        return SessionControlProtocol_1_1.DYN_TEMP_REF_MESSAGE;
    }

    public boolean shouldConvert(Field field) {
        return field.getClass().equals(DynamicTemplateReference.class);
    }

    public Group[] getTemplateExchangeTemplates() {
        return new Group[] { SessionControlProtocol_1_1.DYN_TEMP_REF_INSTR };
    }
}

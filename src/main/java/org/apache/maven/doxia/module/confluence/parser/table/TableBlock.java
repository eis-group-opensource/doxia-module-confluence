/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.apache.maven.doxia.module.confluence.parser.table;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.doxia.module.confluence.parser.AbstractFatherBlock;
import org.apache.maven.doxia.module.confluence.parser.Block;
import org.apache.maven.doxia.sink.Sink;

import java.util.List;

/**
 * @version $Id: TableBlock.java 1090706 2011-04-09 23:15:28Z hboutemy $
 */
class TableBlock
    extends AbstractFatherBlock
{
    TableBlock( List<Block> childBlocks )
    {
        super( childBlocks );
    }

    /** {@inheritDoc} */
    public  void before(  Sink sink )
    {
        sink.table();
        sink.tableRows( getJustification(), false );
    }

    /** {@inheritDoc} */
    public  void after(  Sink sink )
    {
        sink.tableRows_();
        sink.table_();
    }

    private int[] getJustification()
    {
        final AbstractFatherBlock b = ( (AbstractFatherBlock) getBlocks().get( 0 ) );
        int[] justification = new int[b.getBlocks().size()];
        for ( int i = 0; i < justification.length; i++ )
        {
            justification[i] = Sink.JUSTIFY_CENTER;
        }

        return justification;
    }
}

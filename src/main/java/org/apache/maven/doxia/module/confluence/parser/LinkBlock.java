/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.apache.maven.doxia.module.confluence.parser;

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

import org.apache.maven.doxia.sink.Sink;

/**
 * @version $Id: LinkBlock.java 763762 2009-04-09 18:19:56Z ltheussl $
 */
class LinkBlock
    implements Block
{
    private  String reference;

    private  String text;

    LinkBlock(  String reference,  String text )
    {
        if ( reference == null || text == null )
        {
            throw new IllegalArgumentException( "arguments can't be null" );
        }
        this.reference = reference;
        this.text = text;
    }

    /** {@inheritDoc} */
    public  void traverse(  Sink sink )
    {
        sink.link( reference );

        sink.text( text );

        sink.link_();
    }
}

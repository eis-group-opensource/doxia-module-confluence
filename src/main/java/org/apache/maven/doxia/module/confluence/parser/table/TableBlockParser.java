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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.doxia.util.ByLineReaderSource;
import org.apache.maven.doxia.module.confluence.ConfluenceMarkup;
import org.apache.maven.doxia.module.confluence.parser.FigureBlockParser;
import org.apache.maven.doxia.module.confluence.parser.ParagraphBlockParser;
import org.apache.maven.doxia.module.confluence.parser.SectionBlockParser;
import org.apache.maven.doxia.util.ByLineSource;
import org.apache.maven.doxia.module.confluence.parser.BlockParser;
import org.apache.maven.doxia.module.confluence.parser.Block;
import org.apache.maven.doxia.module.confluence.parser.BoldBlock;
import org.apache.maven.doxia.parser.ParseException;
import org.codehaus.plexus.util.StringUtils;

/**
 * Parse tables
 *
 * @author Juan F. Codagnone
 * @version $Id: TableBlockParser.java 1090706 2011-04-09 23:15:28Z hboutemy $
 */
public class TableBlockParser
    implements BlockParser
{
    private static final String EMPTY_STRING = "";
    private static final String ANY_CHARACTER = ".*";
    private static final String ESCAPE_CHARACTER = "\\";

    /** {@inheritDoc} */
    public  boolean accept( String line, ByLineSource source )
    {
        return line.startsWith( "|" );
    }

    /** {@inheritDoc} */
    public  Block visit(  String line,  ByLineSource source )
        throws ParseException
    {
        if ( !accept( line, source ) )
        {
            throw new IllegalAccessError( "call accept before this ;)" );
        }

        List<Block> rows = new ArrayList<Block>();

        String l = line;

        do
        {
            l = l.substring( 0, l.lastIndexOf( "|" ) );

            List<Block> cells = new ArrayList<Block>();

            BlockParser headingParser = new SectionBlockParser();
            BlockParser figureParser = new FigureBlockParser();
            BlockParser[] subparsers = new BlockParser[] { headingParser, figureParser };
            BlockParser paragraphParser = new ParagraphBlockParser( subparsers );

            if ( l.startsWith( "||" ) )
            {
                String[] text = StringUtils.split( l, "||" );


                for ( int i = 0; i < text.length; i++ )
                {
                    List<Block> textBlocks = new ArrayList<Block>();

                    textBlocks.add( ( ( ParagraphBlockParser) paragraphParser )
                            .visit(text[i], new ByLineReaderSource( new StringReader( EMPTY_STRING ) ), false ) );

                    List<Block> blocks = new ArrayList<Block>();

                    blocks.add( new BoldBlock( textBlocks ) );

                    cells.add( new TableCellHeaderBlock( blocks ) );
                }
            }
            else
            {
                int it = 0;
                String[] text = StringUtils.split( l, "|" );
                List<String> texts = new LinkedList<String>();


                while ( it < text.length )
                {
                    if ( text[it].matches( ANY_CHARACTER + ESCAPE_CHARACTER
                                + ConfluenceMarkup.LINK_START_MARKUP + ANY_CHARACTER )
                            && !text[it].matches( ANY_CHARACTER + ESCAPE_CHARACTER
                                + ConfluenceMarkup.LINK_END_MARKUP + ANY_CHARACTER ) )
                    {
                        texts.add( text[it] + ConfluenceMarkup.TABLE_CELL_MARKUP + text[it + 1] );
                        it += 2;
                        continue;
                     }
                    texts.add( text[it] );
                    it++;
                }

                Object[] pText = texts.toArray();
                for ( int i = 0; i < pText.length; i++ )
                {
                    List<Block> blocks = new ArrayList<Block>();

                    blocks.add( ( (ParagraphBlockParser) paragraphParser ).visit( (String) pText[i],
                            new ByLineReaderSource( new StringReader( EMPTY_STRING ) ), false ) );

                    cells.add( new TableCellBlock( blocks ) );
                }
            }

            rows.add( new TableRowBlock( cells ) );
        }

        while ( ( l = source.getNextLine() ) != null && accept( l, source ) );

        assert rows.size() >= 1;

        return new TableBlock( rows );
    }
}

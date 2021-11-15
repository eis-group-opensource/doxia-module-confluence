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

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;

/**
 * Re-usable builder that can be used to generate paragraph and list item text from a string containing all the content
 * and wiki formatting. This class is intentionally stateful, but cheap to create, so create one as needed and keep it
 * on the stack to preserve stateless behaviour in the caller.
 *
 * @author Dave Syer
 * @version $Id: ChildBlocksBuilder.java 1090706 2011-04-09 23:15:28Z hboutemy $
 * @since 1.1
 */
public class ChildBlocksBuilder
{
    private boolean insideBold = false;

    private boolean insideItalic = false;

    private boolean insideLink = false;

    private List<Block> blocks = new ArrayList<Block>();

    private StringBuffer text = new StringBuffer();

    private String input;

    private boolean insideMonospaced;

    /**
     * <p>Constructor for ChildBlocksBuilder.</p>
     *
     * @param input the input.
     */
    public ChildBlocksBuilder( String input )
    {
        this.input = input;
    }

    /**
     * Utility method to convert marked up content into blocks for rendering.
     *
     * @return a list of Blocks that can be used to render it
     */
    public List<Block> getBlocks()
    {
        List<Block> specialBlocks = new ArrayList<Block>();

        for ( int i = 0; i < input.length(); i++ )
        {
            char c = input.charAt( i );

            switch ( c )
            {
                case '*':
                    if( insideMonospaced ) {
                        text.append( c ); // avoid screwing with code samples and so on
                        break;
                    }
                    
                    if ( insideBold )
                    {
                        // check if there's trailing whitespace
                        if ( nextCharMatches( input, i, " _\t.,", true ) ) {
                            insideBold = false;
                            specialBlocks = getList( new BoldBlock( getChildren( text, specialBlocks ) ), specialBlocks );
                            text = new StringBuffer();
                        }
                        else {
                            text.append( c ); // just add the char
                        }
                    }
                    else
                    {
                        // check if should turn on bold
                        if ( charBeforeMatches( input, i, " _\t", true ) ) {
                            // only process as italic if there's preceding whitespace (don't screw up all_the_programming_variable_names)
                            text = addTextBlockIfNecessary( blocks, specialBlocks, text );
                            insideBold = true;
                        }
                        else {
                            text.append( c ); // just add the char
                        }
                    }

                    break;
                case '_':
                    if( insideMonospaced ) {
                        text.append( c ); // avoid screwing with code samples and so on
                        break;
                    }
                    
                    if ( insideItalic )
                    {
                        // check if there's trailing whitespace
                        if ( nextCharMatches( input, i, " *\t.,", true ) ) {
                            insideItalic = false;
                            specialBlocks = getList( new ItalicBlock( getChildren( text, specialBlocks ) ), specialBlocks );
                            text = new StringBuffer();
                        }
                        else {
                            text.append( c ); // just add the char
                        }
                    }
                    else
                    {
                        // check if should turn on italic
                        if ( charBeforeMatches( input, i, " *\t", true ) ) {
                            // only process as italic if there's preceding whitespace (don't screw up all_the_programming_variable_names)
                            text = addTextBlockIfNecessary( blocks, specialBlocks, text );
                            insideItalic = true;
                        }
                        else {
                            text.append( c ); // just add the char
                        }
                    }

                    break;
                case '[':
                    if( insideMonospaced ) {
                        // don't care about links while in monospaced mode
                        text.append( c );
                        break;
                    }
                    
                    insideLink = true;
                    text = addTextBlockIfNecessary( blocks, specialBlocks, text );
                    break;
                case ']':
                    if( insideMonospaced ) {
                        // don't care about links while in monospaced mode
                        text.append( c );
                        break;
                    }
                    
                    if ( insideLink )
                    {
                        boolean addHTMLSuffix = false;
                        String link = text.toString();

                        if ( !link.endsWith( ".html" ) )
                        {
                            if ( link.indexOf( "http" ) < 0 )
                            {
                                addHTMLSuffix = true;
                            }
                        }
                        if ( link.indexOf( "|" ) > 0 )
                        {
                            String[] pieces = StringUtils.split( text.toString(), "|" );
                            
                            if ( pieces[1].startsWith("^") )
                            {
                                // use the "file attachment" ^ syntax to force verbatim link: needed to allow actually linking to some non-html resources
                                pieces[1] = pieces[1].substring(1); // now just get rid of the lead ^
                                addHTMLSuffix = false;  // force verbatim link to support attaching files/resources (not just .html files) 
                            } 

                            if ( addHTMLSuffix )
                            {
                                if ( pieces[1].indexOf( "#" ) < 0 )
                                {
                                    pieces[1] = pieces[1].concat( ".html" );
                                }
                                else
                                {
                                    if ( !pieces[1].startsWith( "#" ) )
                                    {
                                        String[] temp = pieces[1].split( "#" );
                                        pieces[1] = temp[0] + ".html#" + temp[1];
                                    }
                                }
                            }

                            blocks.add( new LinkBlock( pieces[1], pieces[0] ) );
                        }
                        else
                        {
                            String value = link;

                            if ( link.startsWith( "#" ) )
                            {
                                value = link.substring( 1 );
                            }
                            else if ( link.startsWith( "^" ) )
                            {
                                link = link.substring( 1 );  // chop off the lead ^ from link and from value
                                value = link;
                                addHTMLSuffix = false; // force verbatim link to support attaching files/resources (not just .html files) 
                            }

                            if ( addHTMLSuffix )
                            {
                                if ( link.indexOf( "#" ) < 0 )
                                {
                                    link = link.concat( ".html" );
                                }
                                else
                                {
                                    if ( !link.startsWith( "#" ) )
                                    {
                                        String[] temp = link.split( "#" );
                                        link = temp[0] + ".html#" + temp[1];
                                    }
                                }
                            }

                            blocks.add( new LinkBlock( link, value ) );
                        }

                        text = new StringBuffer();
                        insideLink = false;
                    }

                    break;
                case '{':
                    if( insideMonospaced ) {
                        // don't care about blocks in monospace mode
                        text.append( c );
                        break;
                    }
                    
                    // use look-back to allow escaping some occurences of { }
                    if( charBefore( input, i) == '\\' ) {
                        // escape this occurence
                        text.setLength( text.length() - 1);
                        text.append( c );
                        break;
                    }

                    // not in monospace, and not escaped:
                    
                    text = addTextBlockIfNecessary( blocks, specialBlocks, text );

                    if ( nextCharAt( input, i ) == '{' ) // it's a new monospaced block
                    {
                        i++;
                        insideMonospaced = true;
                    }
                    // else it's a confluence macro...

                    break;
                case '}':
                    
                    if( insideMonospaced )
                    {
                        if( nextCharAt( input, i ) == '}' 
                                && nextCharAt( input, i+1) != '}' ) // handle special case of }}} - we want to be "greedy" here  
                        {
                            // this is monospace block to be terminated
                            i++;
                            insideMonospaced = false;
                            specialBlocks = getList( new MonospaceBlock( getChildren( text, specialBlocks ) ),
                                                     specialBlocks );
                            text = new StringBuffer();
                            break;
                        }
                        else {
                            // just append the char
                            text.append( c );
                            break;
                        }
                    }
                    
                    // not in monospace

                    // use look-back to allow escaping some occurences of { }
                    if( charBefore( input, i) == '\\' ) {
                        // escape this occurence
                        text.setLength( text.length() - 1);
                        text.append( c );
                        break;
                    }

                    // else it's a macro...
                    
                    String name = text.toString();
                    if ( name.startsWith( "anchor:" ) )
                    {
                        blocks.add( new AnchorBlock( name.substring( "anchor:".length() ) ) );
                    }
                    else
                    {
                        blocks.add( new TextBlock( "{" + name + "}" ) );
                    }
                    text = new StringBuffer();
                    break;
                    
                case '\\':

                    // System.out.println( "line = " + line );
                    if( insideMonospaced )
                    {
                        // just add the char, don't try to do linebreaks inside monospace (as side effect, allows us to write "\\unc\path\names" without undue hassle)
                        text.append( c );
                    }
                    else 
                    {
                        if ( nextCharAt( input, i ) == '\\' )
                        {
                            // process two \\ slashes as explicit line break
                            i++;
                            text = addTextBlockIfNecessary( blocks, specialBlocks, text );
                            blocks.add( new LinebreakBlock() );
                        }
                        else if( nextCharMatches( input, i, "*_", false ) )
                        {
                            // eat the slash and append the escaped char verbatim (also prevent it getting processed)
                            i++;
                            text.append( input.charAt( i ) );
                        } 
                        else {
                            text.append( c );
                        }
                    }

                    break;
                default:
                    text.append( c );
            }

            if ( !specialBlocks.isEmpty() )
            {
                if ( !insideItalic && !insideBold && !insideMonospaced )
                {
                    blocks.addAll( specialBlocks );
                    specialBlocks.clear();
                }
            }

        }

        if ( text.length() > 0 )
        {
            blocks.add( new TextBlock( text.toString() ) );
        }

        return blocks;
    }

    private List<Block> getList( Block block, List<Block> currentBlocks )
    {
        List<Block> list = new ArrayList<Block>();

        if ( insideBold || insideItalic || insideMonospaced )
        {
            list.addAll( currentBlocks );
        }

        list.add( block );

        return list;
    }

    private List<Block> getChildren( StringBuffer buffer, List<Block> currentBlocks )
    {
        String txt = buffer.toString().trim();

        if ( currentBlocks.isEmpty() && StringUtils.isEmpty( txt ) )
        {
            return new ArrayList<Block>();
        }

        ArrayList<Block> list = new ArrayList<Block>();

        if ( !insideBold && !insideItalic && !insideMonospaced )
        {
            list.addAll( currentBlocks );
        }

        if ( StringUtils.isEmpty( txt ) )
        {
            return list;
        }

        list.add( new TextBlock( txt ) );

        return list;
    }

    /** return next char */
    private static char nextCharAt( final String input, final int i )
    {
        return input.length() > i + 1 ? input.charAt( i + 1 ) : '\0';
    }

    /** return previous char (backtrack) */
    private static char charBefore( final String input, final int i )
    {
        return i > 0 ? input.charAt( i - 1 ) : '\0';
    }
    
    /** check if next char matches the chars in given String of chars.
     * 
     * @param charList characters to match
     * @param matchEol if end of line should be treated as matching char
     */
    private static boolean nextCharMatches( final String input, final int i, final String charList, final boolean matchEol )
    {
        final char c = nextCharAt( input, i );
        if( c == 0 ) {
            return matchEol;
        }
        
        for ( int j = 0; j < charList.length(); j++ )
        {
            if( c == charList.charAt( j ) ) {
                return true;
            }
        }
        
        return false;
    }

    /** check if previous char matches the chars in given String of chars
     * @param charList characters to match
     * @param matchSol if start of line should be treated as matching char
     */
    private static boolean charBeforeMatches( final String input, final int i, final String charList, final boolean matchSol )
    {
        final char c = charBefore( input, i );
        if( c == 0 ) {
            return matchSol;
        }
        
        for ( int j = 0; j < charList.length(); j++ )
        {
            if( c == charList.charAt( j ) ) {
                return true;
            }
        }
        
        return false;
    }
    
    private StringBuffer addTextBlockIfNecessary( List<Block> blcks, List<Block> specialBlocks, StringBuffer txt )
    {
        if ( txt.length() == 0 )
        {
            return txt;
        }

        TextBlock textBlock = new TextBlock( txt.toString() );

        if ( !insideBold && !insideItalic && !insideMonospaced )
        {
            blcks.add( textBlock );
        }
        else
        {
            specialBlocks.add( textBlock );
        }

        return new StringBuffer();
    }

}

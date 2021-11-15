/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.apache.maven.doxia.module.confluence;

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

import org.apache.maven.doxia.markup.TextMarkup;

/**
 * This interface defines all markups and syntaxes used by the <b>Confluence</b> format.
 *
 * See <a href="http://confluence.atlassian.com/display/CONF25/Confluence+Notation+Guide+Overview">
 * Confluence Notation Guide Overview</a>
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id: ConfluenceMarkup.java 706268 2008-10-20 12:49:35Z vsiveton $
 * @since 1.0
 */
public interface ConfluenceMarkup
    extends TextMarkup
{
    // ----------------------------------------------------------------------
    // Confluence markups
    // ----------------------------------------------------------------------

    /** Syntax for the anchor : "{anchor:" */
    String ANCHOR_START_MARKUP = "{anchor:";

    /** Syntax for the anchor : "}" */
    String ANCHOR_END_MARKUP = "}";

    /** Syntax for the bold markup: "*" */
    String BOLD_END_MARKUP = "*";

    /** Syntax for the bold markup: "*" */
    String BOLD_START_MARKUP = "*";

    /** Syntax for the figure markup: "!" */
    String FIGURE_END_MARKUP = "!";

    /** Syntax for the figure markup: "!" */
    String FIGURE_START_MARKUP = "!";

    /** Syntax for the italic markup: "_" */
    String ITALIC_END_MARKUP = "_";

    /** Syntax for the italic markup: "_" */
    String ITALIC_START_MARKUP = "_";

    /** Syntax for the line break markup: "\\\\" */
    String LINE_BREAK_MARKUP = "\\\\";

    /** Syntax for the link end markup: "]" */
    String LINK_END_MARKUP = "]";

    /** Syntax for the link middle markup: "|" */
    String LINK_MIDDLE_MARKUP = "|";

    /** Syntax for the link start markup: "[" */
    String LINK_START_MARKUP = "[";

    /** Syntax for the list item markup: "* */
    String LIST_ITEM_MARKUP = "* ";

    /** Syntax for the mono-spaced style end: "{{" */
    String MONOSPACED_END_MARKUP = "{{";

    /** Syntax for the mono-spaced style start: "}}" */
    String MONOSPACED_START_MARKUP = "}}";

    /** Syntax for the numbering decimal markup char: "1." */
    String NUMBERING_MARKUP = "1.";

    /** Syntax for the table cell header end markup: "|" */
    String TABLE_CELL_HEADER_END_MARKUP = "|";

    /** Syntax for the table cell header start markup: "|" */
    String TABLE_CELL_HEADER_START_MARKUP = "|";

    /** Syntax for the table cell markup: "|" */
    String TABLE_CELL_MARKUP = "|";

    /** Syntax for the table row markup: "|" */
    String TABLE_ROW_MARKUP = "|";
}

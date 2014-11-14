package org.codehaus.mojo.dbunit;

/*
 * The MIT License
 *
 * Copyright (c) 2006, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
*/

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.dbunit.ant.Export;
import org.dbunit.ant.Query;
import org.dbunit.ant.Table;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatDtdDataSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Execute DbUnit Export DTD operation
 *
 * @author <a href="mailto:gdyangyu@gmail.com">Yu Yang</a>
 * @version $Id: ExportDtdMojo.java 9171 2014-11-14 18:21:11Z Yu Yang $
 * @goal exportDtd
 */
public class ExportDtdMojo
        extends AbstractDbUnitMojo {
    /**
     * Location of exported DTD file
     *
     * @parameter expression="${dest}" default-value="${basedir}/src/test/resources/dataset.dtd"
     */
    protected File dest;

    /**
     * Encoding of exported data.
     *
     * @parameter expression="${encoding}" default-value="${project.build.sourceEncoding}"
     */
    protected String encoding;


    public void execute()
            throws MojoExecutionException, MojoFailureException {
        if (skip) {
            this.getLog().info("Skip export execution");
            return;
        }

        super.execute();

        try {
            //dbunit require dest directory is ready
            dest.getParentFile().mkdirs();

            IDatabaseConnection connection = createConnection();
            try {
                IDataSet dataSet = new FilteredDataSet(new DatabaseSequenceFilter(connection),
                        connection.createDataSet());
                Writer out = new OutputStreamWriter(new FileOutputStream(dest), encoding);
                FlatDtdDataSet.write(dataSet, out);

            } finally {
                connection.close();
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Error executing export", e);
        }

    }
}

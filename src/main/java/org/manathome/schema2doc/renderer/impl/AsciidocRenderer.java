package org.manathome.schema2doc.renderer.impl;

import org.manathome.schema2doc.augmenter.IDocumentationAugmenter;
import org.manathome.schema2doc.augmenter.ITableDataAugmenter;
import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.renderer.RenderException;
import org.manathome.schema2doc.scanner.IDbColumn;
import org.manathome.schema2doc.scanner.IDbPrivilege;
import org.manathome.schema2doc.scanner.IDbProcedure;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.scanner.IReference;
import org.manathome.schema2doc.scanner.ITableReference;
import org.manathome.schema2doc.util.Convert;
import org.manathome.schema2doc.util.NotNull;
import org.manathome.schema2doc.util.Require;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.stream.Collectors;

import javax.sql.rowset.CachedRowSet;

/**
 * generating asciidoc output.
 *
 * @see    <a href="http://asciidoctor.org">asciidoctor.org</a>
 * @author man-at-home
 */
public class AsciidocRenderer implements IRenderer {

    private static final Logger LOG = LoggerFactory.getLogger(AsciidocRenderer.class);
    
    /** out stream to render to. */
    private PrintWriter out = null;
    
    /** add plantuml (http://plantuml.com/) diagram to table output. */
    private boolean includeGraphics = true;

    /** .ctor. */
	public AsciidocRenderer(@NotNull final PrintWriter out) {
		this();
		this.setOut(out);
	}
	
    /** .ctor. */
	public AsciidocRenderer() {
		LOG.debug("using asciidoc renderer..");		
	}
	
    /** .ctor. */
	public AsciidocRenderer(boolean includeGraphics) {
		this();
		this.includeGraphics = includeGraphics;
	}	

	@Override
	public void renderCatalog(@NotNull String catalog) {
		Require.notNull(out, "out").println("== Catalog " + catalog);
		LOG.debug("render catalog " + catalog);
	}

	@Override
	public void renderSchema(@NotNull String schema) {
		Require.notNull(out, "out").println("=== Schema " + schema);
		LOG.debug("render schema " + schema);
	}	
	
	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.renderer.IRenderer#beginRenderTable(org.manathome.schema2doc.scanner.IDbTable)
	 */
	@Override
	public void beginRenderTable(@NotNull IDbTable table, IDocumentationAugmenter tableDocAugmenter) {
		Require.notNull(out, "out").println("[[" +  createTableFQN(table) + "]]"); // xrefable id
		
		out.println("==== Table " + 
//				 	Convert.nvl2(Require.notNull(table, "table").getCatalog(), table.getCatalog() + ".", "") +
//					Convert.nvl2(table.getSchema(), table.getSchema() + "." , "") +
					"*" + table.getName() + "*");
		
		out.println(Convert.nvl(table.getComment(), ""));		
		out.println("");
		
		if (tableDocAugmenter != null) {
			out.print(Convert.nvl(tableDocAugmenter.getData(), ""));
		}
		
		if (includeGraphics) {
			renderDiagram(table);
		}
		
		// begin table columns..
		out.println("|===");
		out.println("|Column | PK | Type | Comment | Size | Constraints");
		out.println(""); // needed to get header formatting for above line.
		out.flush();
	}

	/** render diagram inline for table. */
	private void renderDiagram(@NotNull final IDbTable table) {
		out.println("");
		out.println("[plantuml, images/" + Require.notNull(table, "table").fqnName() + ".diagram, png]");     
		out.println("....");

		out.println("object " + table.getName() + " {");
		table.getColumns()
			 .filter(clmn -> clmn.isPrimaryKey())
			 .forEach(clmn -> out.println(" + " + clmn.getName()));
		table.getColumns()
		 .filter(clmn -> clmn.getForeignKeyReferences().anyMatch(c -> true))
		 .forEach(clmn -> out.println(" - " + clmn.getName()));
		out.println("}");

		out.println("....");
		out.println("");
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.renderer.IRenderer#endRenderTable(org.manathome.schema2doc.scanner.IDbTable)
	 */
	@Override
	public void endRenderTable(@NotNull final IDbTable table, ITableDataAugmenter tableDataAugmenter) {
		Require.notNull(out, "out").println("|==="); // end columns table
		out.println();
		out.println("Grants: " + table.getPrivileges().map(IDbPrivilege::display).collect(Collectors.joining(", ")));
		out.println();
		out.println("Referenced by: " + 
				table.getReferrer()
					 .map(ref -> createReferenceXref(ref))
					 .collect(Collectors.joining(", ")));
		
		if (tableDataAugmenter != null && tableDataAugmenter.getData() != null) {
			renderRowSet(tableDataAugmenter.getData());
		}
		out.flush();
	}
	

	private void renderRowSet(final CachedRowSet rowSet) {
		
		if (rowSet != null && rowSet.size() > 0) {
			LOG.debug("rendering additional data for table");
			out.println();
			out.println("===== Data");
			out.println();
			out.println("|===");
			
			try {				
				final int columnCount = rowSet.getMetaData().getColumnCount();
				
				for (int i = 1; i <= columnCount; i++) {
					out.print("| " + rowSet.getMetaData().getColumnLabel(i));
				}
				out.println(); // needed to get header formatting for above line.
				out.println();
				
				// data rows
				while (rowSet.next()) {
					for (int i = 1; i <= columnCount; i++) {
						renderTableCell(rowSet.getString(i));
					}
					out.println();
				}	
			} catch (SQLException ex) {
				throw new RenderException("could not render data table" + ex.getMessage(), ex);
			} finally {
				out.println("|===");
				out.println();
				out.flush();
			}
		}
	}

	/** render one asciidoc table cell. */
	private void renderTableCell(final String cellContent) {
		out.println("| " + escapeText(cellContent));		
	}
	
	/** escape characters that would lead to asciidoc formatting. (unfinished). */
	private String escapeText(final String unescapedText) {
		return Convert
				.nvl(unescapedText, "")
				.replace("\n---", "\n\\---")	// that is *not* the full story..
				.replace("|", "\\|")			// still undecided: full escape OR oracle comments with asciidoc formatting?
				.replace("\r\n", " +")
				.replace("\n", " +")
				;				
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.renderer.IRenderer#renderColumn(org.manathome.schema2doc.scanner.IDbColumn)
	 */
	@Override
	public void renderColumn(@NotNull final IDbColumn column) {
		Require.notNull(column, "column");
		Require.notNull(out, "out");
		renderTableCell((column.isPrimaryKey() ? "*" + column.getName() + "*" : column.getName())); 
		renderTableCell((column.isPrimaryKey() ? "PK " + column.getPrimaryKeyIndex() : ""));
		renderTableCell(column.getTypename()); 
		renderTableCell(Convert.nvl(column.getComment(), ""));
		renderTableCell((column.getSize() != null ? column.getSize().toString() : ""));
		
		if (column.getForeignKeyReferences().findAny().isPresent()) {
			out.print("| ");
			column.getForeignKeyReferences().forEach(fk -> {
				out.print(column.getForeignKeyReferences().findAny().get().getName() 
						+ " to "
						+ createTableXref(
								fk.getReferencedCatalog(), 
								fk.getReferencedSchema(), 
								fk.getReferencedTable(), null));
			});				
		} else {
			renderTableCell("");
		}
		out.println("");
	}
	

	@Override
	public void beginRenderDocumentation(IDocumentationAugmenter docAugmenter) {
		LOG.debug("render document");
		Require.notNull(out, "out").println("= schema2doc database documentation");
		out.println(":Date:    " + new Date());
		out.println(":numbered:"); 
		out.println(":icons:     font");
		out.println(":toc:       left");
		out.println(":toclevels: 4");
		out.println(":description: asciidoc database schema documentation generated by the schema2doc tool.");
		out.println("");
		out.println("WARNING: schema2doc is not production ready yet!");
		
		if (docAugmenter != null) {
			out.println("");
			out.println("");
			out.println(Convert.nvl(docAugmenter.getData(), ""));
		}
	}

	@Override
	public void endRenderDocumentation() {
		LOG.debug("render document ended.");
		Require.notNull(out, "out").println("");
		out.println("document generated at " + new Date());
		out.flush();
	}	

	@Override
	public void close() throws Exception {
		if (out != null) {
			out.flush();
			out.close();
		}
		out = null;
	}
	
	/** create an asciidoc xref  table link in form <<(fqnTable,displayText>>.
	 * @see http://asciidoctor.org/docs/asciidoc-writers-guide/ 
	 */
	static String createTableXref(String catalog, String schema, @NotNull String table, String optionalDisplayText) {
		return "<<" + 
			   createTableFQN(catalog, schema, table) +
			   "," + 
			   Convert.nvl(optionalDisplayText, table) +
			   ">>";
	}
	
	/** create an asiidoc xref if possible. */
	static String createReferenceXref(@NotNull IReference reference) {
		if (Require.notNull(reference, "reference") instanceof ITableReference) {
			ITableReference tblReference = (ITableReference) reference;
			return createTableXref(tblReference.getCatalog(), tblReference.getSchema(), tblReference.getTable(), null);
		} else {
			return reference.display();
		}
	}
	
	static String createTableFQN(@NotNull IDbTable table) {
		return createTableFQN(Require.notNull(table).getCatalog(), table.getSchema(), table.getName());
	}
	
	static String createTableFQN(String catalog, String schema, @NotNull String table) {
		return
		   Convert.nvl2(catalog, catalog + ".", "") +
		   Convert.nvl2(schema, schema + ".", "") +
		   Require.notNull(table, "table")
		   ;
	}

	@Override
	public String getSuggestedFilename() {
		return "schemadocumentation.s2d.adoc";
	}

	@Override
	public void setOut(PrintWriter out) {
		this.out = Require.notNull(out, "out (PrintWriter)");
	}

	@Override
	public void renderProcedure(@NotNull IDbProcedure procedure) {

		out.println("| " + Require.notNull(procedure).getName());
		out.println("| " + procedure.getComment());
		out.println();		
	}

	@Override
	public void beginRenderCode() {
		Require.notNull(out).println();
		out.println();
		out.println("==== Procedures "); 
		out.println();
		out.println("|===");
		out.println("| name | comment");
		out.println();	
	}

	@Override
	public void endRenderCode() {
		out.println("|===");
		out.println();
		out.println();
	}
}

/**
* DISCLAIMER
* Copyright 2016 Mentor Graphics Corporation. All Rights Reserved. Recipients may
* use this code to serve as an example for creating Java or Java Script plugins 
* for Mentor Graphics products. Recipients may duplicate, modify, reproduce, 
* translate, use, and distribute this code or any part thereof, or disclose or
* provide this code or any part thereof to third parties, provided that all notices
* (including this disclaimer)are fully reproduced with and remain in the code. 
* THIS CODE IS MADE AVAILABLE "AS IS," WITH ALL FAULTS AND WITHOUT WARRANTY OR 
* SUPPORT OF ANY KIND. NEITHER MENTOR GRAPHICS NOR ITS LICENSORS MAKE ANY WARRANTY,
* EXPRESS, IMPLIED OR STATUTORY, WITH RESPECT TO THIS CODE, AND MENTOR GRAPHICS AND
* ITS LICENSORS SPECIFICALLY DISCLAIM ALL IMPLIED WARRANTIES OF MERCHANTABILITY, 
* FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT OF INTELLECTUAL PROPERTY. IN
* NO EVENT SHALL MENTOR GRAPHICS OR ITS LICENSORS BE LIABLE FOR DAMAGES OF ANY KIND
* ARISING OUT OF THE USE OF THIS CODE, WHETHER BASED ON CONTRACT, TORT, OR ANY OTHER
* LEGAL THEORY.
*

**/

package RobinSpliceBOMTableProvider;

import com.mentor.chs.api.IXAttributes;
import com.mentor.chs.api.IXConnectivityObject;
import com.mentor.chs.api.sbom.IXCostResult;
import com.mentor.chs.api.sbom.IXSubAssembly;
import com.mentor.chs.api.workbook.IXSubAssemblyWorkbookContext;
import com.mentor.chs.api.workbook.data.IXCellData;
import com.mentor.chs.api.workbook.data.IXTableData;
import com.mentor.chs.plugin.workbook.provider.IXWorkbookProvider;
import Comparator.AlphaNumComparator;
import com.mentor.chs.api.IXAdditionalComponent;
import com.mentor.chs.api.IXOtherComponent;
import com.mentor.chs.api.IXSplice;
import com.mentor.chs.api.IXWire;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BOMTableProvider implements IXWorkbookProvider<IXTableData>
{

	private static final int HEADER_ROWS_COUNT = 1;
	private static final int COLUMN_COUNT = 6;
	private static final double POINTONE = 0.1;

	public BOMTableProvider()
	{
	}

	@Override public String getDescription()
	{
		return "This provider returns a table BOM material of this subassembly";
	}

	@Override public String getName()
	{
		return "BOM material of this subassembly";
	}

	@Override public String getVersion()
	{
		return "1";
	}

	public IXTableData generate(IXSubAssemblyWorkbookContext context, Map<String, String> paramsMap)
	{
		List<? extends IXCostResult> results = getGenerationData(context);
		return convertResultsToTable(context, results);
	}

	private List<? extends IXCostResult> getGenerationData(IXSubAssemblyWorkbookContext context)
	{
            Collection<? extends IXSubAssembly> subAssemblyList  = context.getCurrentSubAssemblies();
            for (IXSubAssembly subAssembly : subAssemblyList) 
            {
                Collection<? extends IXSubAssembly> childSubAssemblys=   subAssembly.getChildren();
                for(IXSubAssembly assembly:childSubAssemblys )
                {
                    if(assembly.getHarnessObject() instanceof IXSplice){
                        IXSplice splice= (IXSplice)assembly.getHarnessObject();
                        
                    }
                    else if (assembly.getHarnessObject() instanceof IXWire){}
                    else if(assembly.getHarnessObject() instanceof IXOtherComponent){}
                    else if(assembly.getHarnessObject() instanceof IXAdditionalComponent){}
                
                
                }
            
            }
		List<IXCostResult> results = new LinkedList<>();
		context.getCurrentSubAssemblies().forEach(s -> results.addAll(context.getContextualView(s).getTasks()));
		Collections.sort(results, new ResultOrderComparator());
		return results;
	}

	@Override public Class<IXTableData> getGenerateDataType()
	{
		return IXTableData.class;
	}

	private IXTableData convertResultsToTable(IXSubAssemblyWorkbookContext context, List<? extends IXCostResult> results)
	{
		IXTableData tableData = context.getProviderDataFactory().createTableData();
		tableData.setColumnCount(COLUMN_COUNT);
		tableData.setNumberOfHeaderRows(HEADER_ROWS_COUNT);

		tableData.addRow(createTableHeader(context));

		for (IXCostResult result : results) {
			tableData.addRow(convertToTableRow(result, context));
		}
		return tableData;
	}

	private Collection<? extends IXCellData> createTableHeader(IXSubAssemblyWorkbookContext context)
	{
		List<IXCellData> row = new LinkedList<IXCellData>();
		row.add(convertDataToTableCell(context, "Task Description"));
		row.add(convertDataToTableCell(context, "Matched Object"));
		row.add(convertDataToTableCell(context, "Time"));
		row.add(convertDataToTableCell(context, "Cost Center"));
		row.add(convertDataToTableCell(context, "Resource"));
		row.add(convertDataToTableCell(context, "Part No."));
		return row;
	}

	private Collection<? extends IXCellData> convertToTableRow(IXCostResult data, IXSubAssemblyWorkbookContext context)
	{
		List<IXCellData> row = new LinkedList<IXCellData>();
		row.add(convertDataToTableCell(context, data.getAttribute(IXAttributes.Description)));
		row.add(convertDataToTableCell(context, data.getAttribute(IXAttributes.MatchedObjectName)));
		row.add(convertDataToTableCell(context, formatStringNumber(data.getAttribute(IXAttributes.CostResultValue))));
		row.add(convertDataToTableCell(context, data.getAttribute(IXAttributes.CostCenterName)));
		row.add(convertDataToTableCell(context, data.getAttribute(IXAttributes.SBOMResourceName)));
		row.add(convertDataToTableCell(context, getPartNumberOfMatchedObject(data)));
		return row;
	}

	private String formatStringNumber(String number)
	{
		if(number == null) {
			return "";
		}
		try {
			double value = Double.parseDouble(number);
			return formatDoubleNumber(value);
		}
		catch(NumberFormatException ignored) {
			return number;
		}
	}

	private String formatDoubleNumber(double d)
	{
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);
		if (d < POINTONE && d > 0) {
			// Ensure the number of significant decimal places is equal to the current
			// maximum fraction digits
			int oldMaxFraction = format.getMaximumFractionDigits();
			int newMaxFraction = oldMaxFraction;
			double value = d * 10;
			while (value < 1.0) {
				newMaxFraction++;
				value *= 10;
			}
			format.setMaximumFractionDigits(newMaxFraction);
			String result = format.format(d);
			format.setMaximumFractionDigits(oldMaxFraction);
			return result;
		}
		else {
			return format.format(d);
		}
	}

	private IXCellData convertDataToTableCell(IXSubAssemblyWorkbookContext context, String data)
	{
		IXCellData cell = context.getProviderDataFactory().createTableCellData();
		cell.setValue(data);
		cell.setColumnSpan(1);
		return cell;
	}

	private String getPartNumberOfMatchedObject(IXCostResult result)
	{
		IXSubAssembly sub = result.getMatchedObject();
		String part;
		if(sub.getHarnessObject() != null) {
			IXConnectivityObject rawObject = sub.getHarnessObject();
			part = rawObject.getAttribute(IXAttributes.PartNumber);
		}
		else {
			part = sub.getAttribute(IXAttributes.PartNumber);
		}
		if(part == null) {
			return "";
		}
		return part;
	}

	private static class ResultOrderComparator implements Comparator<IXCostResult>
	{
		@Override public int compare(IXCostResult o1, IXCostResult o2)
		{
			final String ownerName1 = o1.getOwnerSubAssembly().getAttribute(IXAttributes.Name);
			final String ownerName2 = o2.getOwnerSubAssembly().getAttribute(IXAttributes.Name);
			AlphaNumComparator<String> nameComparator = new AlphaNumComparator<String>(false);
			int compareResult = nameComparator.compare(ownerName1, ownerName2);
			if(compareResult == 0) {
				compareResult = nameComparator.compare(o1.getAttribute(IXAttributes.CostResultOrder),
						o2.getAttribute(IXAttributes.CostResultOrder));
				if(compareResult == 0) {
					compareResult = nameComparator.compare(o1.getAttribute(IXAttributes.FormulaName),
							o2.getAttribute(IXAttributes.FormulaName));
				}
			}
			return compareResult;
		}
	}
}


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Robin XU
 * @Email robin_xu@mentor.com
 */

package RobinSpliceBOMTableProvider;

import com.mentor.chs.api.IXAbstractPin;
import com.mentor.chs.api.IXCavity;
import com.mentor.chs.api.IXCavityDetail;
import com.mentor.chs.api.IXCavityPlug;
import com.mentor.chs.api.IXCavitySeal;
import com.mentor.chs.api.IXConnectivityObject;
import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXLibraryCustomerPartNumber;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.api.IXTerminal;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.api.IXWireEnd;
import com.mentor.chs.api.sbom.IXSubAssembly;
import com.mentor.chs.api.workbook.IXSubAssemblyWorkbookContext;
import com.mentor.chs.api.workbook.data.IXCellData;
import com.mentor.chs.api.workbook.data.IXProviderDataFactory;
import com.mentor.chs.api.workbook.data.IXTableData;
import com.mentor.chs.plugin.workbook.provider.IXWorkbookProvider;
import BOMExampleOne.BasePlugin;
import BOMExampleOne.AlphaNumComparator;
import BOMExampleOne.AlphaNumComparator;
import BOMExampleOne.AlphaNumComparator;
import BOMExampleOne.BasePlugin;
import BOMExampleOne.BasePlugin;
import com.mentor.chs.api.IXAdditionalComponent;
import com.mentor.chs.api.IXOtherComponent;
import com.mentor.chs.api.IXSplice;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RobinSpliceBOMTablePro extends BasePlugin
  implements IXWorkbookProvider<IXTableData>
{
  private static final int NUMBER_OF_COLUMNS = 4;
  private static final int[] headerSpan = { 1,1,1,1 };
  private static final int NUMBER_OF_HEADER_ROWS = 2;
  private final String[] header = { "0rient/方向", "Object/零件", "KANBAN/看板", "PN/零件号" };
  
  //private final String[] secondaryHeader = { "Index","Name", "P/N", "Name", "P/N","C.P/N", "Length", "derivate","CSA", "Col.","Conn", "Strip", "Terminal", "Seal","plug","Conn", "Strip", "Terminal", "Seal","plug" };
  public int index=0;
  
  public RobinSpliceBOMTablePro()
  {
    super("Splice BOM For HG", "2016SP1.2-V2.0", "Splice BOM workbook Provider for HG");
  }

  public IXTableData generate(IXSubAssemblyWorkbookContext context, Map<String, String> paramsMap)
  {
    IXTableData tableData = context.getProviderDataFactory().createTableData();
    tableData.setColumnCount(NUMBER_OF_COLUMNS);
    try {
      Collection subAssemblyList = context.getCurrentSubAssemblies();

      List tableRowDataList = new LinkedList();
      Comparator comparator = new AlphaNumComparator();

      for (Iterator i$ = subAssemblyList.iterator(); i$.hasNext(); ) 
      { 
        IXSubAssembly subAssembly = (IXSubAssembly)i$.next();
        List<IXWire> wireList = getWires(subAssembly);
        Collections.sort(wireList, comparator);
        for (IXWire wire : wireList)
        {
            tableRowDataList.add(new TableRowData(subAssembly, wire));
        }
        
        List<IXSplice> spliceList=getSplices(subAssembly);
        Collections.sort(spliceList, comparator);
        for (IXSplice splice : spliceList)
        {
            tableRowDataList.add(new SpliceTableRowData(subAssembly, splice));
        }
        
        List<IXAdditionalComponent> AdditionalCompList=getAdditionalComponent(subAssembly);
        Collections.sort(AdditionalCompList, comparator);
        for (IXAdditionalComponent AdditionalComonet : AdditionalCompList)
        {
            tableRowDataList.add(new AdditionalComponentTableRowData(subAssembly, AdditionalComonet));
        }
        
        List<IXOtherComponent> otherComponent=getOtherComponent(subAssembly);
        Collections.sort(otherComponent, comparator);
        for (IXOtherComponent otherComp : otherComponent)
            {
                tableRowDataList.add(new OtherComponentTableRowData(subAssembly, otherComp));
            }
        
        List<IXLibraryObject> OperationComponent=getOperationComponent(subAssembly);
        Collections.sort(OperationComponent, comparator);
        for (IXLibraryObject operationComp : OperationComponent)
            {
                tableRowDataList.add(new OperationComponentTableRowData(subAssembly, operationComp));
            }
        
        
      }
      IXSubAssembly subAssembly;
      String opName = "";
      int opIndex = context.getCurrentOperationIndex();
      List opNames = context.getOperationNames();
      if ((opNames != null) && (opIndex >= 0) && (opIndex < opNames.size()) && (!opNames.isEmpty())) {
        opName = (String)opNames.get(opIndex);
      }

      createTableHeaders(tableData, context.getProviderDataFactory(), opName);
      
      for ( Object data :tableRowDataList) 
        {
            if(data instanceof TableRowData)
            {
                TableRowData WireData=(TableRowData)data;
                tableData.addRow(WireData.toList(context.getProviderDataFactory()));
            }
             if(data instanceof OtherComponentTableRowData)
            {
                OtherComponentTableRowData otherCompoentData=(OtherComponentTableRowData)data;
                tableData.addRow(otherCompoentData.toList(context.getProviderDataFactory()));
            }
             if(data instanceof AdditionalComponentTableRowData)
            {
                AdditionalComponentTableRowData otherCompoentData=(AdditionalComponentTableRowData)data;
                tableData.addRow(otherCompoentData.toList(context.getProviderDataFactory()));
            }
            
            if(data instanceof SpliceTableRowData)
            {
                SpliceTableRowData SpliceData=(SpliceTableRowData)data;
                tableData.addRow(SpliceData.toList(context.getProviderDataFactory()));
            }
            
            if(data instanceof OperationComponentTableRowData)
            {
                OperationComponentTableRowData opearationComponentData=(OperationComponentTableRowData)data;
                tableData.addRow(opearationComponentData.toList(context.getProviderDataFactory()));
            }
            
            
            
        }
      
    }
    catch (NullPointerException e)
    {
      e.printStackTrace();
    }

    return tableData;
  }
  private static class AdditionalComponentTableRowData
  {
    public static final String NOT_AVAILABLE_MARKER = "";
   
    private IXSubAssembly subAssembly;
    private IXAdditionalComponent AddCom;
    private int index=1;
    private Set<IXSplice> Splices;
   
    private AdditionalComponentTableRowData(IXSubAssembly s, IXAdditionalComponent ac)
    {
      this.subAssembly = s;
      this.AddCom = ac;  
    }
  
    public String getAdditionalComponentPN()
    {
      return this.AddCom.getAttribute("PartNumber");
    }
    
    public String getAddComName()
    {
      return this.AddCom.getAttribute("Name");
    }
    
   public String getSubAssemblyPN()
    {
      return this.subAssembly.getAttribute("PartNumber");
    }

    public Collection<IXCellData> toList(IXProviderDataFactory providerDataFactory)
    {
      
      String[] values = {"T",getAddComName(),"-",getAdditionalComponentPN()};

      Collection cells = new ArrayList(NUMBER_OF_COLUMNS);
      for (String value : values) {
        IXCellData cell = providerDataFactory.createTableCellData();
        cell.setValue(value);
        cells.add(cell);
      }
      return cells;
    }
  }
  
  
  private static class OtherComponentTableRowData
  {
    public static final String NOT_AVAILABLE_MARKER = "";
   
    private IXSubAssembly subAssembly;
    private IXOtherComponent otherCom;
    private int index=1;
    private Set<IXSplice> Splices;
   
    private OtherComponentTableRowData(IXSubAssembly sub, IXOtherComponent oth)
    {
      this.subAssembly = sub;
      this.otherCom = oth;  
    }
  
    public String getOtherComponentPN()
    {
      return this.otherCom.getAttribute("PartNumber");
    }
    
    public String getOtherComponentName()
    {
      return this.otherCom.getAttribute("Name");
    }
    
   public String getSubAssemblyPN()
    {
      return this.subAssembly.getAttribute("PartNumber");
    }

    public Collection<IXCellData> toList(IXProviderDataFactory providerDataFactory)
    {
      
      String[] values = {"T",getOtherComponentName(),"-",getOtherComponentPN()};

      Collection cells = new ArrayList(NUMBER_OF_COLUMNS);
      for (String value : values) {
        IXCellData cell = providerDataFactory.createTableCellData();
        cell.setValue(value);
        cells.add(cell);
      }
      return cells;
    }
  }
  
  private static class SpliceTableRowData
  {
    public static final String NOT_AVAILABLE_MARKER = "";
   
    private IXSubAssembly subAssembly;
    private IXSplice Splice;
    private int index=1;
    private Set<IXSplice> Splices;
   
    private SpliceTableRowData(IXSubAssembly s, IXSplice w)
    {
      this.subAssembly = s;
      this.Splice = w;  
    }
  
    public String getSplicePN()
    {
      return this.Splice.getAttribute("PartNumber");
    }
    
    public String getSubAssemblyName()
    {
      return this.subAssembly.getAttribute("Name");
    }
    
   public String getSubAssemblyPN()
    {
      return this.subAssembly.getAttribute("PartNumber");
    }

    public Collection<IXCellData> toList(IXProviderDataFactory providerDataFactory)
    {
      
      String[] values = {"X",getSubAssemblyName(),getSubAssemblyPN(),getSplicePN()};

      Collection cells = new ArrayList(NUMBER_OF_COLUMNS);
      for (String value : values) {
        IXCellData cell = providerDataFactory.createTableCellData();
        cell.setValue(value);
        cells.add(cell);
      }
      return cells;
    }
  }
  
  private static class OperationComponentTableRowData
  {
    public static final String NOT_AVAILABLE_MARKER = "";
   
    private IXSubAssembly subAssembly;
    private IXLibraryObject otherCom;
    private int index=1;
    private Set<IXSplice> Splices;
   
    private OperationComponentTableRowData(IXSubAssembly sub, IXLibraryObject oth)
    {
      this.subAssembly = sub;
      this.otherCom = oth;  
    }
  
    public String getOperationPN()
    {
      return this.otherCom.getAttribute("PartNumber");
    }
    
    public String getOperationComponentName()
    {
      return this.otherCom.getAttribute("GroupName");
    }
    
   public String getSubAssemblyPN()
    {
      return this.subAssembly.getAttribute("PartNumber");
    }

    public Collection<IXCellData> toList(IXProviderDataFactory providerDataFactory)
    {
      
      String[] values = {"T",getOperationComponentName(),"-",getOperationPN()};

      Collection cells = new ArrayList(NUMBER_OF_COLUMNS);
      for (String value : values) {
        IXCellData cell = providerDataFactory.createTableCellData();
        cell.setValue(value);
        cells.add(cell);
      }
      return cells;
    }
  }

  private void createTableHeaders(IXTableData t, IXProviderDataFactory providerDataFactory, String operation)
  {
    t.addRow(Collections.singleton(createCell(providerDataFactory, operation, NUMBER_OF_COLUMNS)));

    Collection headerRow = new ArrayList(NUMBER_OF_COLUMNS);
    for (int i = 0; i < this.header.length; i++) {
      String value = this.header[i];
      int span = headerSpan[i];
      headerRow.add(createCell(providerDataFactory, value, span));
    }
    t.addRow(headerRow);
    t.setNumberOfHeaderRows(NUMBER_OF_HEADER_ROWS);
  }

  private IXCellData createCell(IXProviderDataFactory providerDataFactory, String value, int span)
  {
    IXCellData cell = providerDataFactory.createTableCellData();
    cell.setColumnSpan(span);
    cell.setValue(value);
    return cell;
  }

  private List<IXWire> getWires(IXSubAssembly subAssembly)
  {
    List wireList = new LinkedList();
    for (IXSubAssembly child : subAssembly.getChildren()) {
        IXConnectivityObject harnessObject = child.getHarnessObject();
        if ((harnessObject != null) && ((harnessObject instanceof IXWire))) {
            wireList.add((IXWire)harnessObject);
        }
      
        for (IXSubAssembly childSub : child.getChildren()) {
            IXConnectivityObject ChildHarnessObject = childSub.getHarnessObject();
            if ((ChildHarnessObject != null) && ((ChildHarnessObject instanceof IXWire))) {
              wireList.add((IXWire)ChildHarnessObject);
            }

          }
      
    }
    return wireList;
  }

  public Class<IXTableData> getGenerateDataType()
  {
    return IXTableData.class;
  }

    private List<IXSplice> getSplices(IXSubAssembly subAssembly) {
        List SPliceList = new LinkedList();
    for (IXSubAssembly child : subAssembly.getChildren()) {
      IXConnectivityObject harnessObject = child.getHarnessObject();
      if ((harnessObject != null) && ((harnessObject instanceof IXSplice))) {
        SPliceList.add((IXSplice)harnessObject);
      }
    }
    return SPliceList;//To change body of generated methods, choose Tools | Templates.
    }
     
    private List<IXAdditionalComponent> getAdditionalComponent(IXSubAssembly subAssembly) {
            List AddotionComp = new LinkedList();
            IXConnectivityObject harnessObject = subAssembly.getHarnessObject();
            if ((harnessObject != null) && ((harnessObject instanceof IXAdditionalComponent))) {
                AddotionComp.add((IXAdditionalComponent)harnessObject);
            }
            for (IXSubAssembly child : subAssembly.getChildren()) 
            {
                if(child instanceof IXAdditionalComponent)
                AddotionComp.add((IXAdditionalComponent)child);
                harnessObject = child.getHarnessObject();
                if ((harnessObject != null) && ((harnessObject instanceof IXAdditionalComponent))) {
                        AddotionComp.add((IXAdditionalComponent)harnessObject);
                    }
            }
          return AddotionComp;
    }
    
    private List<IXOtherComponent> getOtherComponent(IXSubAssembly subAssembly) {
            List otherComponent = new LinkedList();
            IXConnectivityObject harnessObject = subAssembly.getHarnessObject();
            if ((harnessObject != null) && ((harnessObject instanceof IXOtherComponent))) {
                otherComponent.add((IXOtherComponent)harnessObject);
            }
            for (IXSubAssembly child : subAssembly.getChildren()) 
            {
                if(child instanceof IXOtherComponent)
                otherComponent.add((IXOtherComponent)child);
                harnessObject = child.getHarnessObject();
                if ((harnessObject != null) && ((harnessObject instanceof IXOtherComponent))) {
                        otherComponent.add((IXOtherComponent)harnessObject);
                    }
            }
          return otherComponent;//To change body of generated methods, choose Tools | Templates.
    }
    
       private List<IXLibraryObject> getOperationComponent(IXSubAssembly subAssembly) {
            List LibraryObject = new LinkedList();    
            IXLibraryObject harnessObject=subAssembly.getOperationComponent();
            if ((harnessObject != null) && ((harnessObject instanceof IXLibraryObject))) {
                        LibraryObject.add((IXOtherComponent)harnessObject);
                    }
            for (IXSubAssembly child : subAssembly.getChildren()) 
            {
                harnessObject=child.getOperationComponent();              
                //harnessObject = child.getHarnessObject();
                if ((harnessObject != null) && ((harnessObject instanceof IXLibraryObject))) {
                        LibraryObject.add((IXLibraryObject)harnessObject);
                    }
            }
          return LibraryObject;//To change body of generated methods, choose Tools | Templates.
    }
    

  private static class TableRowData
  {
    public static final String NOT_AVAILABLE_MARKER = "";
    private IXSubAssembly subAssembly;
    private IXWire wire;

    private int index=1;


    private TableRowData(IXSubAssembly s, IXWire w)
    {
      this.subAssembly = s;
      this.wire = w;
    }
    public IXSubAssembly getSubAssembly()
    {
      return this.subAssembly;
    }

    public String getSubAssemblyName()
    {
      return this.subAssembly.getAttribute("Name");
    }

    public String getSubAssemblyPN()
    {
      return this.subAssembly.getAttribute("PartNumber");
    }

    public String getWireName()
    {
      return this.wire.getAttribute("Name");
    }

    public String getWirePINname()
    {
        Set<IXWireEnd> wireends=this.wire.getWireEnds();
         String pinName="",SplicePin="";
        for(IXWireEnd wireend: wireends)
        {
           pinName= wireend.getPin().getAttribute("Name");
           if(pinName.equals("L")||pinName.equals("R")||pinName.equals("X"))
           {
               SplicePin=pinName;
           }
        }
      return SplicePin;
    }

    
    public String getWirePN()
    {
      return this.wire.getAttribute("PartNumber");
    }
    
     public String getWireKANBAN()
    {
     String WireKANBAN=null;
     for (IXSubAssembly child : subAssembly.getChildren()) 
        {
         IXConnectivityObject harnessObject = child.getHarnessObject();
         //WireKANBAN=child.getAttribute("PartNumber");
         //WireKANBAN=child.getAttribute("PartNumber");
         for (IXSubAssembly childSub : child.getChildren()) {
                 IXConnectivityObject childharnessObject = childSub.getHarnessObject();
                 if ((childharnessObject != null) && ((childharnessObject instanceof IXWire))) {
                    IXWire wire= (IXWire)childharnessObject;
                    if(this.wire.getAttribute("Name").equals(wire.getAttribute("Name")))
                    {
                        WireKANBAN=child.getAttribute("PartNumber");
                    }
                 }

          }
//         if ( ((harnessObject instanceof IXWire))) {
//               IXWire wire= (IXWire)harnessObject;
//               if(this.wire.getAttribute("Name").equals(wire.getAttribute("Name")))
//               {
//                   WireKANBAN=child.getAttribute("PartNumber");
//               }
//         }
      
       }
        
      return WireKANBAN;
    }
    
    
    public Collection<IXCellData> toList(IXProviderDataFactory providerDataFactory)
    {
      
      String[] values = {getWirePINname(), getWireName(), getWireKANBAN(),  getWirePN()};

      Collection cells = new ArrayList(NUMBER_OF_COLUMNS);
      for (String value : values) {
        IXCellData cell = providerDataFactory.createTableCellData();
        cell.setValue(value);
        cells.add(cell);
      }
      return cells;
    }
  }
}
package dist.esper.experiment2;

import java.util.*;

import dist.esper.core.util.NumberFormatter;
import dist.esper.epl.expr.OperatorTypeEnum;
import dist.esper.experiment.util.MultiLineFileWriter;
import dist.esper.experiment2.data.NodesParameter;
import dist.esper.external.event.EventInstanceGenerator;

public class QueryGeneratorMain2 {
	public static void main(String[] args){
		String[] eventNames={"A","B","C","D","E","F","G","H","L","M","N","P","Q",
				"R","S","T","U","V","W","X","Y","Z"};
//		if(args.length<2){
//			System.out.println("error: please specify: number of events, output file path.");
//			return;
//		}
//		int eventCount=Integer.parseInt(args[0]);
		//run(Arrays.copyOf(eventNames, eventCount), args[1]);
		run(Arrays.copyOf(eventNames, 6), "query/queries2_140.txt");
	}
	public static void run(String[] eventNames, String filePath){
		EventInstanceGenerator[] eigs=EventGeneratorFactory2.genEventInstanceGenerators(eventNames);
		OperatorTypeEnum[] filterOpTypes=new OperatorTypeEnum[]{
			OperatorTypeEnum.GREATER,
			OperatorTypeEnum.LESS
		};
		OperatorTypeEnum[] joinOpTypes=new OperatorTypeEnum[]{
			OperatorTypeEnum.GREATER,
			OperatorTypeEnum.EQUAL,
			OperatorTypeEnum.LESS
		};
		int[] windowTimes={60, 120, 180};
		int numSelectElementsPerFilter=3;
		
		//String filePath="query/queries2.txt";
		NodesParameter[] nodeParams=new NodesParameter[]{
			new NodesParameter(1, 60, 15, 0.3, 0.2),
			new NodesParameter(2, 36, 12, 0.3, 0.3),
			new NodesParameter(3, 24, 8, 0.3, 0.3),
			new NodesParameter(4, 10, 5, 0.3, 0.3),
			new NodesParameter(5, 10, 5, 0.3, 0.3),
		};
		QueryGenerator2 queryGen2=new QueryGenerator2(
			eigs, filterOpTypes, joinOpTypes,
			windowTimes, numSelectElementsPerFilter,
			nodeParams
		);
		
		String headers=generateHeaders(eventNames, eigs, filterOpTypes, joinOpTypes,
				windowTimes, numSelectElementsPerFilter,
				nodeParams);
		try {
			List<String> queryList=queryGen2.generateQueries();
			List<String> headerAndQueryList=new ArrayList<String>(queryList.size()+2);
			headerAndQueryList.add(headers);
			headerAndQueryList.addAll(queryList);		
			MultiLineFileWriter.writeToFile(filePath, headerAndQueryList);
			System.out.format("info: generated %d queries, and outputed them into %s", queryList.size(), filePath);
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
//		for(String query: queryList){
//			System.out.println(query);
//			System.out.println();
//		}
	}
	
	public static String generateHeaders(String[] eventNames, EventInstanceGenerator[] eigs,
			OperatorTypeEnum[] filterOpTypes, OperatorTypeEnum[] joinOpTypes,
			int[] windowTimes, int numSelectElementsPerFilter, 
			NodesParameter[] nodeParams){
		StringBuilder sb=new StringBuilder();
		sb.append("# this file is randomly generated by dist.esper.experiment2.QueryGeneratorMain2.");
		sb.append("\n# arguments are follows: ");
		sb.append("\n# \tevent names="+Arrays.toString(eventNames));
		sb.append("\n# \tnumber of properties per event: "+eigs[0].getEvent().getPropList().size());
		sb.append("\n# \tfilter operator types: "+Arrays.toString(filterOpTypes));
		sb.append("\n# \tjoin operator types: "+Arrays.toString(joinOpTypes));
		sb.append("\n# \twindow times(sec): "+Arrays.toString(windowTimes));
		sb.append("\n# \tnumber of select-elements per filter: "+numSelectElementsPerFilter);
		sb.append("\n# \tquery types and arguments: ");
		sb.append("\n# \t\t<number of join-ways> <query total count> <query count per category> <query equivalent ratio> <query implying ratio>");
		for(NodesParameter np: nodeParams){
			sb.append(String.format("\n# \t\t%d\t\t%d\t\t%d\t\t%s\t\t%s", 
					np.numWay, np.nodeCount, np.nodeCountPerType,
					NumberFormatter.format(np.equalRatio), NumberFormatter.format(np.implyRatio)));
		}
		sb.append("\n");
		return sb.toString();
	}
}

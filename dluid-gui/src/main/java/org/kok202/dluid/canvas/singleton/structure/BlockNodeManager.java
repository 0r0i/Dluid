package org.kok202.dluid.canvas.singleton.structure;

import lombok.Data;
import org.kok202.dluid.CanvasFacade;
import org.kok202.dluid.ai.entity.Layer;
import org.kok202.dluid.ai.entity.enumerator.LayerType;
import org.kok202.dluid.application.util.MathUtil;
import org.kok202.dluid.canvas.block.BlockNode;
import org.kok202.dluid.canvas.entity.MergeBlockProperty;
import org.kok202.dluid.domain.structure.GraphManager;
import org.kok202.dluid.domain.structure.GraphNode;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class BlockNodeManager extends GraphManager<BlockNode>{

    public void removeGraphNode(String layerId) {
        GraphNode<BlockNode> targetBlockNodeGraphNode = findGraphNodeByLayerId(layerId);
        targetBlockNodeGraphNode.getData().getBlockLayer().delete();

        // Remove all directly connected pipe layer
        Stream.concat(
                targetBlockNodeGraphNode.getIncomingNodes().stream(),
                targetBlockNodeGraphNode.getOutgoingNodes().stream())
                .forEach(blockNodeGraphNode -> {
                    if (blockNodeGraphNode.getData().getBlockLayer().getType() == LayerType.PIPE_LAYER)
                        removeGraphNode(blockNodeGraphNode.getData().getBlockLayer().getId());
                });

        // Remove me
        removeGraphNode(
                blockNodeObj -> {
                    BlockNode blockNode = (BlockNode) blockNodeObj;
                    return blockNode.getBlockLayer().getId().equals(layerId);
                },
                graphNode -> {
                    GraphNode<BlockNode> blockGraphNode = (GraphNode<BlockNode>) graphNode;
                    blockGraphNode.getData().deleteHexahedrons();
                });

        reshapeAllBlockByType();
    }

    public GraphNode<BlockNode> findGraphNodeByLayerId(String layerId) {
        return findGraphNode(blockNodeObj -> {
            BlockNode blockNode = (BlockNode) blockNodeObj;
            return blockNode.getBlockLayer().getId().equals(layerId);
        });
    }

    public List<GraphNode<BlockNode>> findAllReachableNode(String layerId) {
        return findAllReachableNode(findGraphNodeByLayerId(layerId));
    }

    public void notifyLayerDataChanged(String layerId){
        GraphNode<BlockNode> graphNode = findGraphNodeByLayerId(layerId);
        BlockNode blockNode = graphNode.getData();
        blockNode.reshapeBlockModel();
        reshapeAllBlockByType();
    }

    public void reshapeAllBlockByType(){
        reshapeAllMergeBlock();
        reshapeAllPipeBlock();
    }

    private void reshapeAllMergeBlock(){
        getGraphNodes()
                .stream()
                .filter(graphNodeBlockNode -> graphNodeBlockNode.getData().getBlockLayer().getType() == LayerType.MERGE_LAYER)
                .forEach(graphNodeBlockNode -> {
                    Layer layer = graphNodeBlockNode.getData().getBlockLayer();
                    List<Layer> incomingLayers = CanvasFacade.findIncomingLayers(layer.getId());

                    int inputSize = 0;
                    for (Layer incomingLayer : incomingLayers) {
                        inputSize += incomingLayer.getProperties().getOutputVolume();
                    }
                    inputSize = Math.max(inputSize, 1);

                    List<Integer> recommendedDivisors = MathUtil.getDivisors(inputSize);
                    MergeBlockProperty mergeBlockProperty = (MergeBlockProperty) CanvasFacade.findGraphNodeByLayerId(layer.getId()).getData().getBlockInfo().getExtra();
                    int outputSizeY = recommendedDivisors.get(mergeBlockProperty.getPointingIndex(recommendedDivisors.size()));
                    int outputSizeX = inputSize / outputSizeY;
                    layer.getProperties().setInputSize(outputSizeX, outputSizeY);
                    layer.getProperties().setOutputSize(outputSizeX, outputSizeY);
                    graphNodeBlockNode.getData().reshapeBlockModel();
                });
    }

    private void reshapeAllPipeBlock(){
        getGraphNodes()
                .stream()
                .filter(graphNodeBlockNode -> graphNodeBlockNode.getData().getBlockLayer().getType() == LayerType.PIPE_LAYER)
                .forEach(graphNodeBlockNode -> {
                    GraphNode<BlockNode> sourceGraphNodeBlockNode = graphNodeBlockNode.getIncomingNodes().get(0); // Exist only one. because it is pipe block.
                    GraphNode<BlockNode> destinationGraphNodeBlockNode = graphNodeBlockNode.getOutgoingNodes().get(0); // Exist only one. because it is pipe block.

                    if(destinationGraphNodeBlockNode.getData().getBlockLayer().getType() == LayerType.MERGE_LAYER){
                        int[] sourceOutputSize = sourceGraphNodeBlockNode.getData().getBlockLayer().getProperties().getOutputSize();
                        int[] destinationInputSize = destinationGraphNodeBlockNode.getData().getBlockLayer().getProperties().getInputSize();
                        graphNodeBlockNode.getData().getBlockLayer().getProperties().setInputSize(sourceOutputSize[0], sourceOutputSize[1]);
                        graphNodeBlockNode.getData().getBlockLayer().getProperties().setOutputSize(destinationInputSize[0], destinationInputSize[1]);
                    }
                    else{
                        int[] sourceOutputSize = sourceGraphNodeBlockNode.getData().getBlockLayer().getProperties().getOutputSize();
                        graphNodeBlockNode.getData().getBlockLayer().getProperties().setInputSize(sourceOutputSize[0], sourceOutputSize[1]);
                        graphNodeBlockNode.getData().getBlockLayer().getProperties().setOutputSize(sourceOutputSize[0], sourceOutputSize[1]);
                    }
                    graphNodeBlockNode.getData().reshapeBlockModel();
                });
    }

    public List<GraphNode<BlockNode>> findAllGraphNode(Predicate<? super GraphNode<BlockNode>> predicate){
        return getGraphNodes().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public GraphNode<BlockNode> findStartBlockConnectedWithLayerId(String layerId) {
        GraphNode<BlockNode> startNode = findGraphNodeByLayerId(layerId);
        return findStartBlockConnectedWithLayerIdSearch(startNode);
    }

    private GraphNode<BlockNode> findStartBlockConnectedWithLayerIdSearch(GraphNode<BlockNode> currentNode) {
        if(currentNode.getData().getBlockLayer().getType().isStartLayerType())
            return currentNode;

        List<GraphNode<BlockNode>> incomingNodes = currentNode.getIncomingNodes();
        if(incomingNodes.isEmpty())
            return null;

        for(GraphNode<BlockNode> incomingNode : incomingNodes){
            GraphNode<BlockNode> searchedNode = findStartBlockConnectedWithLayerIdSearch(incomingNode);
            if(searchedNode != null)
                return searchedNode;
        }
        return null;
    }

}

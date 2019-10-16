package org.kok202.deepblock.canvas.content;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.PickResult;
import javafx.scene.shape.Box;
import org.kok202.deepblock.ai.entity.Layer;
import org.kok202.deepblock.ai.entity.enumerator.LayerType;
import org.kok202.deepblock.application.content.material.insertion.MaterialInsertionInfoHolder;
import org.kok202.deepblock.application.global.AppWidgetSingleton;
import org.kok202.deepblock.canvas.block.BlockNode;
import org.kok202.deepblock.canvas.block.BlockNodeFactory;
import org.kok202.deepblock.canvas.polygon.block.HexahedronBottomFace;
import org.kok202.deepblock.canvas.polygon.block.HexahedronTopFace;
import org.kok202.deepblock.canvas.polygon.block.HexahedronVerticalFace;
import org.kok202.deepblock.canvas.singleton.CanvasConstant;
import org.kok202.deepblock.canvas.singleton.CanvasSingleton;
import org.kok202.deepblock.canvas.util.Math3D;
import org.kok202.deepblock.canvas.util.PickResultNodeUtil;

// PickResult is always not null.
// Although user point at empty space, PickResult returns SubScene node
public class BlockInsertionHandler {
    private Group sceneRoot;
    private HexahedronVerticalFace pastBlockHexahedronVerticalFace;

    public BlockInsertionHandler(Group sceneRoot) {
        this.sceneRoot = sceneRoot;
    }

    public void hoveringListener(MaterialInsertionInfoHolder materialInsertionInfoHolder){
        PickResult pickResult = materialInsertionInfoHolder.getDragEvent().getPickResult();
        Node pickResultNode = pickResult.getIntersectedNode();
        if(pastBlockHexahedronVerticalFace != pickResultNode){
            if(pastBlockHexahedronVerticalFace != null){
                pastBlockHexahedronVerticalFace.setColor(CanvasConstant.CONTEXT_COLOR_POSSIBLE_APPEND);
            }
        }
        if(pickResultNode instanceof HexahedronVerticalFace){
            HexahedronVerticalFace targetFace = (HexahedronVerticalFace) pickResultNode;
            BlockNode targetBlockNode = PickResultNodeUtil.convertToBlockNode(pickResult);
            if(pickResultNode instanceof HexahedronTopFace && targetBlockNode.isPossibleToAppendFront()){
                targetFace.setColor(CanvasConstant.CONTEXT_COLOR_TRY_TO_APPEND);
                pastBlockHexahedronVerticalFace = targetFace;
            }
            else if(pickResultNode instanceof HexahedronBottomFace && targetBlockNode.isPossibleToAppendBack()){
                targetFace.setColor(CanvasConstant.CONTEXT_COLOR_TRY_TO_APPEND);
                pastBlockHexahedronVerticalFace = targetFace;
            }
        }
    }

    public void insertListener(MaterialInsertionInfoHolder materialInsertionInfoHolder){
        PickResult pickResult = materialInsertionInfoHolder.getDragEvent().getPickResult();
        Node pickResultNode = pickResult.getIntersectedNode();
        if(pickResultNode instanceof HexahedronVerticalFace){
            BlockNode targetBlockNode = PickResultNodeUtil.convertToBlockNode(pickResult);

            if(pickResultNode instanceof HexahedronTopFace && targetBlockNode.isPossibleToAppendFront()){
                appendFrontToSpecificBlock(materialInsertionInfoHolder.getLayerType(), targetBlockNode);
            }
            else if(pickResultNode instanceof HexahedronBottomFace && targetBlockNode.isPossibleToAppendBack()){
                appendBackToSpecificBlock(materialInsertionInfoHolder.getLayerType(), targetBlockNode);
            }

            if(pastBlockHexahedronVerticalFace != null){
                pastBlockHexahedronVerticalFace.setColor(CanvasConstant.CONTEXT_COLOR_IMPOSSIBLE_APPEND);
            }
        }
        else if(pickResultNode instanceof CoordinateGiantMesh){
            Point3D targetPoint = pickResult.getIntersectedPoint();
            Point3D cameraPoint = CanvasSingleton.getInstance().getMainCanvas().getMainScene().getCamera().getCurrentPosition();
            Point3D intersectedPoint = Math3D.getIntersectPoint(
                    new Point3D(0,0,1),
                    0,
                    targetPoint,
                    cameraPoint);
            sceneRoot.getChildren().add(new Box(intersectedPoint.getX(), intersectedPoint.getY(), intersectedPoint.getZ()));
            createNewBlock(materialInsertionInfoHolder.getLayerType(), intersectedPoint);
        }
    }

    private void appendFrontToSpecificBlock(LayerType layerType, BlockNode targetBlockNode){
        Point3D selectedBlockPosition = targetBlockNode.getBlockInfo().getPosition();
        Point3D insertingPoint = selectedBlockPosition.add(new Point3D(0, -CanvasConstant.NODE_HEIGHT, 0));

        // Add to global block node set and refresh component box material
        Layer layer = new Layer(layerType);
        BlockNode insertedBlockNode = insertLayerBlockModelToCanvas(layer, insertingPoint);
        CanvasSingleton.getInstance()
                .getBlockNodeManager()
                .linkFromNewData(targetBlockNode, insertedBlockNode);
        AppWidgetSingleton.getInstance()
                .getComponentContainerController()
                .getComponentManager()
                .refreshContainerByLayer(insertedBlockNode.getBlockInfo().getLayer());
    }

    private void appendBackToSpecificBlock(LayerType layerType, BlockNode targetBlockNode){
        Point3D selectedBlockPosition = targetBlockNode.getBlockInfo().getPosition();
        Point3D insertingPoint = selectedBlockPosition.add(new Point3D(0, CanvasConstant.NODE_HEIGHT, 0));

        // Add to global block node set and refresh component box material
        Layer layer = new Layer(layerType);
        BlockNode insertedBlockNode = insertLayerBlockModelToCanvas(layer, insertingPoint);
        CanvasSingleton.getInstance()
                .getBlockNodeManager()
                .linkToNewData(targetBlockNode, insertedBlockNode);
        AppWidgetSingleton.getInstance()
                .getComponentContainerController()
                .getComponentManager()
                .refreshContainerByLayer(insertedBlockNode.getBlockInfo().getLayer());
    }

    private void createNewBlock(LayerType layerType, Point3D insertingPoint){
        // Add to global block node set and refresh component box material
        Layer layer = new Layer(layerType);
        BlockNode insertedBlockNode = insertLayerBlockModelToCanvas(layer, insertingPoint);
        CanvasSingleton.getInstance()
                .getBlockNodeManager()
                .registerSoloNode(insertedBlockNode);
        AppWidgetSingleton.getInstance()
                .getComponentContainerController()
                .getComponentManager()
                .refreshContainerByLayer(insertedBlockNode.getBlockInfo().getLayer());
    }

    private BlockNode insertLayerBlockModelToCanvas(Layer layer, Point3D insertingPoint){
        BlockNode blockNode = BlockNodeFactory.create(layer);
        blockNode.addedToScene(sceneRoot, insertingPoint);
        return blockNode;
    }
}

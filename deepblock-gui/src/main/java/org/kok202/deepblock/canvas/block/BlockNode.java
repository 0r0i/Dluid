package org.kok202.deepblock.canvas.block;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import lombok.*;
import org.kok202.deepblock.ai.entity.Layer;
import org.kok202.deepblock.canvas.polygon.block.BlockHexahedron;
import org.kok202.deepblock.canvas.singleton.CanvasSingleton;

import java.util.ArrayList;
import java.util.List;

@ToString(exclude = {"blockHexahedronList"})
@EqualsAndHashCode(exclude = {"blockHexahedronList"})
// IMPORTANT : Because using hash set.
// Or stack overflow can be generated by bidirectional reference.
public abstract class BlockNode {
    @Getter
    private BlockInfo blockInfo;

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private List<BlockHexahedron> blockHexahedronList;

    public BlockNode(Layer layer) {
        blockInfo = new BlockInfo(layer);
        blockHexahedronList = new ArrayList<>();
    }

    protected void setBlockCover(Color[]... colorsList){
        for(Color[] colors : colorsList)
            blockInfo.getColorsList().add(colors);
        refreshBlockCover();
    }

    public void refreshBlockCover(){
        for(int i = 0; i < blockHexahedronList.size(); i++){
            blockHexahedronList.get(i).setColors(getBlockInfo().getColorsList().get(i));
            blockHexahedronList.get(i).setTextureSources(getBlockInfo().getTextureSourcesList().get(i));
            blockHexahedronList.get(i).refreshBlockCover();
        }
    }

    public void translatePosition(double x, double y, double z){
        Point3D currentPosition = blockInfo.getPosition();
        Point3D targetPosition = currentPosition.add(x,y,z);
        setPosition(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ());
    }
    public Point3D getPosition(){
        return blockInfo.getPosition();
    }

    public void setPosition(double x, double y, double z){
        blockInfo.setPosition(x, y, z);
        blockHexahedronList.forEach(blockHexahedron -> blockHexahedron.setPosition(x,y,z));
    }

    public void addedToScene(Group sceneRoot, Point3D insertingPoint) {
        setPosition(insertingPoint.getX(), insertingPoint.getY(), 0);
        blockHexahedronList.forEach(blockHexahedron -> blockHexahedron.addedToScene(sceneRoot));
    }

    public void deleteBlockModel(){
        Group sceneRoot = CanvasSingleton.getInstance().getMainCanvas().getMainScene().getSceneRoot();
        for(int i = 0; i < blockHexahedronList.size(); i++){
            blockHexahedronList.get(i).removedFromScene(sceneRoot);
            blockHexahedronList.remove(i);
        }
    }

    protected void deleteHexahedron(BlockHexahedron targetHexahedron){
        Group sceneRoot = CanvasSingleton.getInstance().getMainCanvas().getMainScene().getSceneRoot();
        targetHexahedron.removedFromScene(sceneRoot);
    }

    protected abstract void createBlockModel(Layer layer);
    public abstract boolean isPossibleToAppendFront();
    public abstract boolean isPossibleToAppendBack();
}

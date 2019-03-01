package com.ct;

import com.epam.indigo.Indigo;
import com.epam.indigo.IndigoObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

    Indigo indigo  =  new  Indigo();

    @Test
    public void indigo() {
        System.out.println("------------indigo------------");
        IndigoObject mol1 = indigo.loadMolecule("[C@H]1(C(C)=C(C)C(=O)O1)[C@@H](C)CC.O1[C@H]([C@@H](O)CO)C(O)=C(O)C1=O");
        IndigoObject mol2 = indigo.loadMoleculeFromFile("C:\\Users\\Avalon\\Downloads\\ketcher.mol");
        IndigoObject qmol1 = indigo.loadQueryMolecule("C1=CC=CC=C1");

        //化学式比特大小
//        System.out.println("mol1比特大小："+mol1.fingerprint("sim").countBits());
//        System.out.println("mol1比特大小："+mol2.fingerprint("sim").countBits());
//        System.out.println();

        //转换
//        System.out.println("转换为smiles");
//        System.out.println(mol2.smiles());
//        System.out.println("转换为mol 无坐标");
        System.out.println(mol1.molfile());

        //指纹
        System.out.println("相似指纹："+mol1.fingerprint("sim"));
        System.out.println("相似指纹："+mol2.fingerprint("sim"));
        System.out.println("子结构指纹："+mol1.fingerprint("sub"));
        System.out.println("共振结构指纹："+mol1.fingerprint("sub-res"));
        System.out.println("互变异构体指纹："+mol1.fingerprint("sub-tau"));
        System.out.println("完整指纹："+mol1.fingerprint("full"));
        System.out.println();

        //相似度查询
        System.out.println("相似部分数量（不为0）："+indigo.commonBits(mol1.fingerprint("sim"),mol2.fingerprint("sim")));
        System.out.println("默认："+indigo.similarity(mol1, mol2, "tanimoto"));
        System.out.println("默认权重："+indigo.similarity(mol1, mol2, "tversky"));
        System.out.println("指定权重："+indigo.similarity(mol1, mol2, "tversky 0.1 0.9"));
        System.out.println("子结构："+indigo.similarity(mol1, mol2, "euclid-sub"));
        System.out.println();

        //完全匹配 分别为：电子分布，原子同位素，立体化学，连接片段，互相匹配，没有条件，所有条件，不带任何参数
        //均可指定 两个分子的原子之间允许的最大均方根偏差 以埃为单位(除TAU均支持) 需要有  xyx 坐标
        // ALL 可以带参数 如： ALL -ELE 匹配所有条件但是忽略电子分布
        IndigoObject match1 = indigo.exactMatch(mol1, mol2, "ELE");
        IndigoObject match2 = indigo.exactMatch(mol1, mol2, "MAS");
        IndigoObject match3 = indigo.exactMatch(mol1, mol2, "STE");
        IndigoObject match4 = indigo.exactMatch(mol1, mol2, "FRA");
        IndigoObject match5 = indigo.exactMatch(mol1, mol2, "TAU");
        IndigoObject match6 = indigo.exactMatch(mol1, mol2, "NONE");
        IndigoObject match7 = indigo.exactMatch(mol1, mol2, "ALL");
        IndigoObject match8 = indigo.exactMatch(mol1, mol2);

        if (match1 != null){
            System.out.println("电子分布匹配--成功");
        }
        if (match2 != null){
            System.out.println("原子同位素匹配--成功");
        }
        if (match3 != null){
            System.out.println("立体化学匹配--成功");
        }
        if (match4 != null){
            System.out.println("连接片段匹配--成功");
        }
        if (match5 != null){
            System.out.println("互相匹配匹配--成功");
        }
        if (match6 != null){
            System.out.println("没有条件匹配--成功");
        }
        if (match7 != null){
            System.out.println("所有条件匹配--成功");
        }
        if (match8 != null){
            System.out.println("不带任何参数匹配--成功");
        }
        System.out.println();


        //子结构匹配 分别为：共振结构匹配 ，互变异构体匹配，INCHI互变异构体匹配，RSMARTS互变异构体匹配
        IndigoObject smatch1 = indigo.substructureMatcher(mol2,"RES").match(qmol1);
        IndigoObject smatch2 = indigo.substructureMatcher(mol2,"TAU").match(qmol1);
        IndigoObject smatch3 = indigo.substructureMatcher(mol2,"TAU INCHI").match(qmol1);
        IndigoObject smatch4 = indigo.substructureMatcher(mol2,"TAU RSMARTS").match(qmol1);

        if (smatch1 !=null){
            System.out.println("共振结构匹配--成功");
        }
        if (smatch2 !=null){
            System.out.println("互变异构体匹配--成功");
        }
        if (smatch3 !=null){
            System.out.println("INCHI互变异构体匹配--成功");
        }
        if (smatch4 !=null){
            System.out.println("RSMARTS互变异构体匹配--成功");
        }
    }

}

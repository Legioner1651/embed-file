package ru.ruslan.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.rhino.IR;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JavaScriptService {

    private final FileService fileServiceInstance;
    private static FileService fileService;

    @PostConstruct
    private void init() {
        fileService = this.fileServiceInstance;
    }

    public static Node parseJsCode(String jsCode) {
        Compiler compiler = new Compiler();
        CompilerOptions options = new CompilerOptions();
        compiler.initOptions(options);
        return compiler.parse(SourceFile.fromCode("input.js", jsCode));
    }

    public static String toJsCode(Node node, boolean isPretty) {
        Compiler compiler = new Compiler();
        CompilerOptions options = new CompilerOptions();
        options.setPrettyPrint(isPretty);
        return compiler.toSource(node);
    }

    public static void insertBeforeByName(Node root, String targetName, Node newNode) {
        Node target = findNodeByName(root, targetName);
        if (target != null) {
            // ИСПРАВЛЕНО: В современном Closure Compiler API новый узел сам вызывает метод insertBefore(target)
            newNode.insertBefore(target);
            log.info("Узел успешно добавлен перед элементом: {}", targetName);
        } else {
            log.warn("Целевой узел {} для вставки не найден", targetName);
        }
    }

    public static void deleteNodeByName(Node root, String targetName) {
        Node target = findNodeByName(root, targetName);
        if (target != null) {
            // Удаление из дерева выполняется через метод detach()
            target.detach();
            log.info("Узел с именем {} успешно удален из AST", targetName);
        } else {
            log.warn("Узел с именем {} для удаления не найден", targetName);
        }
    }

    public static void replaceNodeByName(Node root, String targetName, Node newNode) {
        Node target = findNodeByName(root, targetName);
        if (target != null) {
            // ИСПРАВЛЕНО: Вместо parent.replaceChild используется современный метод target.replaceWith(newNode)
            target.replaceWith(newNode);
            log.info("Узел с именем {} заменен новым узлом", targetName);
        } else {
            log.warn("Узел с именем {} для замены не найден", targetName);
        }
    }

    public static Node findNodeByName(Node node, String name) {
        if (node.isName() && name.equals(node.getString())) {
            return node;
        }
        for (Node child = node.getFirstChild(); child != null; child = child.getNext()) {
            Node found = findNodeByName(child, name);
            if (found != null) return found;
        }
        return null;
    }

    public static List<Node> findNodesByType(Node node, Token type) {
        List<Node> result = new ArrayList<>();
        findNodesByTypeRecursive(node, type, result);
        return result;
    }

    private static void findNodesByTypeRecursive(Node node, Token type, List<Node> accumulator) {
        if (node.getToken() == type) {
            accumulator.add(node);
        }
        for (Node child = node.getFirstChild(); child != null; child = child.getNext()) {
            findNodesByTypeRecursive(child, type, accumulator);
        }
    }

    public static void replaceReadyNode(Node root, Node jsonAstPatch) {
        if (root.hasChildren()) {
            Node firstChild = root.getFirstChild();
            // ИСПРАВЛЕНО: Использование современного метода replaceWith вместо старого родительского replaceChild
            firstChild.replaceWith(jsonAstPatch);
        }
    }

    public static Node convertJsonToAst(JsonNode jsonNode) {
        if (jsonNode.isTextual()) {
            return IR.string(jsonNode.asText());
        } else if (jsonNode.isNumber()) {
            return IR.number(jsonNode.asDouble());
        } else if (jsonNode.isBoolean()) {
            return jsonNode.asBoolean() ? IR.trueNode() : IR.falseNode();
        } else {
            return IR.empty();
        }
    }
}

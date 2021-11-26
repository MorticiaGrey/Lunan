if args[1] == nil then
    print("usage: regcommand [FILE]")
    return
end

path = lunan.getSelectedPath() .. args[1]
if not lunan.registerCommand(path) then
    print(args[1] .. ": no such file or directory")
end
